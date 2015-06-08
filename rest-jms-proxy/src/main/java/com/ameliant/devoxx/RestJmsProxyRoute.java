package com.ameliant.devoxx;

import org.apache.camel.builder.RouteBuilder;

/**
 * @author jkorab
 */
public class RestJmsProxyRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        rest()
            .get()
                .path("/orders/{id}")
                .route().routeId("rest.orders")
                .to("direct:fetchOrders");

        from("direct:fetchOrders").routeId("fetchOrders")
            .transform(simple("I got your order right here: ${header[id]}"));
    }

}
