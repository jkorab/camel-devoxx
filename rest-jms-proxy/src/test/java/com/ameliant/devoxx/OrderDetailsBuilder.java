package com.ameliant.devoxx;

import com.ameliant.devoxx.model.Order;
import com.ameliant.devoxx.model.OrderDetails;
import com.ameliant.devoxx.model.OrderStatus;

import java.math.BigDecimal;

/**
 * @author jkorab
 */
public class OrderDetailsBuilder {

    public OrderDetails buildOrderDetails(String orderId) {
        OrderDetails orderDetails = new OrderDetails();
        Order order = new Order();
        order.setOrderId(orderId);
        order.setItem("Apache Camel Developer's Cookbook");
        order.setQuantity(10);
        order.setPrice(new BigDecimal("30.99"));
        orderDetails.setOrder(order);
        orderDetails.setOrderStatus(OrderStatus.New);
        return orderDetails;
    }

}
