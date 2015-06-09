package com.ameliant.devoxx;

import com.ameliant.devoxx.model.OrderQuery;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JaxbDataFormat;

/**
 * @author jkorab
 */
public class RestJmsProxyRoute extends RouteBuilder {

    private String backendUri;

    public void setBackendUri(String backendUri) {
        this.backendUri = backendUri;
    }

    @Override
    public void configure() throws Exception {
        rest()
            .get()
                .path("/orders/{id}")
                .route().routeId("rest.orders")
                .to("direct:fetchOrders");

        JaxbDataFormat jaxb = new JaxbDataFormat();
        jaxb.setContextPath("com.ameliant.devoxx.model");

        from("direct:fetchOrders").routeId("fetchOrders")
            //.transform(simple("I got your order right here: ${header[id]}"))
            .process(exchange -> {
                Message in = exchange.getIn();
                String id = in.getHeader("id", String.class);
                OrderQuery orderQuery = new OrderQuery();
                orderQuery.setOrderId(id);
                in.setBody(orderQuery);
            })
            .marshal(jaxb)
            .log("${body}")
            .to(backendUri) // returns XML response
            .log("${body}");
    }

}
