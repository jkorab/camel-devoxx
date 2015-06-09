package com.ameliant.devoxx;

import com.ameliant.devoxx.model.OrderDetails;
import com.ameliant.devoxx.model.OrderQuery;
import com.ameliant.devoxx.model.OrderStatus;
import com.ameliant.devoxx.util.EmbeddedActiveMQBroker;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jetty8.JettyHttpComponent8;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.dataformat.JaxbDataFormat;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Rule;
import org.junit.Test;

public class RestJmsProxyRouteTransportTest extends CamelTestSupport {

    public static final String MOCK_BACKEND = "mock:backend";

    @Rule
    public EmbeddedActiveMQBroker embeddedBroker = new EmbeddedActiveMQBroker("myBroker");

    @Override
    protected CamelContext createCamelContext() throws Exception {
        DefaultCamelContext camelContext = new DefaultCamelContext();
        camelContext.addComponent("jetty", new JettyHttpComponent8());

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(embeddedBroker.getTcpConnectorUri());

        ActiveMQComponent activeMQComponent = new ActiveMQComponent();
        activeMQComponent.setConnectionFactory(connectionFactory);
        camelContext.addComponent("jms", activeMQComponent);

        return camelContext;
    }

    @Override
    protected RouteBuilder[] createRouteBuilders() throws Exception {
        RestJmsProxyRoute proxyRoute = new RestJmsProxyRoute();
        proxyRoute.setBackendUri("jms:backend");

        RestConfigRoute restConfigRoute = new RestConfigRoute();
        int jettyPort = restConfigRoute.getJettyPort();

        return new RouteBuilder[] {
                proxyRoute,
                restConfigRoute,
                new RouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        JaxbDataFormat jaxb = new JaxbDataFormat();
                        jaxb.setContextPath("com.ameliant.devoxx.model");

                        // add this to engage the http component
                        // see https://camel.apache.org/http.html
                        // see https://camel.apache.org/jetty.html
                        from("direct:in").routeId("harness.inbound")
                            .setHeader(Exchange.HTTP_PATH,
                                    simple("/orders/${header[id]}"))
                            .log("Calling ${header[" + Exchange.HTTP_PATH + "]}")
                            .to("jetty:http://localhost:" + jettyPort)
                            .unmarshal(jaxb);

                        from("jms:backend").routeId("harness.backend")
                            .unmarshal(jaxb) // OrderQuery
                            .log("Backend received: ${body}")
                            .to(MOCK_BACKEND)
                            .marshal(jaxb);

                    }
                }
        };
    }

    @Test
    public void testFetchOrders() {
        MockEndpoint mockBackend = getMockEndpoint(MOCK_BACKEND);
        mockBackend.whenAnyExchangeReceived((exchange) -> {
            Message in = exchange.getIn();
            OrderQuery orderQuery = in.getBody(OrderQuery.class);
            String orderId = orderQuery.getOrderId();

            OrderDetails orderDetails = new OrderDetailsBuilder().buildOrderDetails(orderId);
            in.setBody(orderDetails);
        });

        OrderDetails response = template.requestBodyAndHeader("direct:in", null, "id", "123", OrderDetails.class);

        assertNotNull(response);
        assertEquals(OrderStatus.New, response.getOrderStatus());
        assertEquals("123", response.getOrder().getOrderId());
    }

}