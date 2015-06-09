package com.ameliant.devoxx.stub;

import com.ameliant.devoxx.model.OrderDetails;
import com.ameliant.devoxx.model.OrderQuery;
import com.ameliant.devoxx.model.util.OrderDetailsBuilder;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JaxbDataFormat;

/**
 * @author jkorab
 */
public class BackendStubRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        JaxbDataFormat jaxb = new JaxbDataFormat();
        jaxb.setContextPath("com.ameliant.devoxx.model");

        from("jms:backend").id("stub.backend")
            .unmarshal(jaxb)
            .process(exchange -> {
                Message in = exchange.getIn();
                OrderQuery orderQuery = in.getBody(OrderQuery.class);
                String orderId = orderQuery.getOrderId();

                OrderDetails orderDetails = new OrderDetailsBuilder().buildOrderDetails(orderId);
                in.setBody(orderDetails);
            })
            .log("Stub replying with: ${body}")
            .marshal(jaxb);
    }
}
