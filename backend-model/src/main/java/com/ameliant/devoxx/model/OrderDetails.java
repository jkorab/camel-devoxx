package com.ameliant.devoxx.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author jkorab
 */
@XmlRootElement(name = "orderDetails")
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderDetails {

    private Order order;

    @XmlElement(name = "status")
    private OrderStatus orderStatus;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
