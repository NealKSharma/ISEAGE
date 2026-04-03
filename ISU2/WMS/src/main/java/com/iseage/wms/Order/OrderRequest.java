package com.iseage.wms.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderRequest {
    private Order order;
    private List<OrderItems> orderItems = new ArrayList<>();

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public List<OrderItems> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItems> orderItems) { this.orderItems = orderItems; }
}