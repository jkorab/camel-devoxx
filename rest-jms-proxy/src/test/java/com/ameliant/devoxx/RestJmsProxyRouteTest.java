package com.ameliant.devoxx;

import com.ameliant.devoxx.model.OrderDetails;
import com.ameliant.devoxx.model.OrderStatus;
import com.ameliant.devoxx.model.util.OrderDetailsBuilder;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class RestJmsProxyRouteTest extends CamelTestSupport {

    public static final String MOCK_BACKEND = "mock:backend";

    @Override
    protected RouteBuilder[] createRouteBuilders() throws Exception {
        RestJmsProxyRoute proxyRoute = new RestJmsProxyRoute();
        proxyRoute.setBackendUri(MOCK_BACKEND);

        return new RouteBuilder[] {proxyRoute, new RestConfigRoute()};
    }

    @Test
    public void testFetchOrders() {
        MockEndpoint mockBackend = getMockEndpoint(MOCK_BACKEND);
        mockBackend.whenAnyExchangeReceived(exchange -> {
            Message in = exchange.getIn();
            String orderId = in.getHeader("id", String.class);

            OrderDetails orderDetails = new OrderDetailsBuilder().buildOrderDetails(orderId);
            in.setBody(orderDetails);
        });

        OrderDetails response = template.requestBodyAndHeader("direct:fetchOrders", null, "id", "123", OrderDetails.class);

        assertNotNull(response);
        assertEquals(OrderStatus.New, response.getOrderStatus());
        assertEquals("123", response.getOrder().getOrderId());
    }

}