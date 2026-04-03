package com.iseage.wms.Order;

public class OrderItems{
    private Long order_id;
    private Long item_id;
    private int ordered_quantity;
    private double item_price;

    public OrderItems() {}

    public OrderItems(Long item_id, int ordered_quantity) {
        this.item_id = item_id;
        this.ordered_quantity = ordered_quantity;
    }

    public Long getOrder_id() { return order_id; }
    public void setOrder_id(Long order_id) { this.order_id = order_id; }
    public Long getItem_id() { return item_id; }
    public void setItem_id(Long item_id) { this.item_id = item_id; }
    public int getOrdered_quantity() { return ordered_quantity; }
    public void setOrdered_quantity(int ordered_quantity) { this.ordered_quantity = ordered_quantity; }
    public double getItem_price() { return item_price; }
    public void setItem_price(double item_price) { this.item_price = item_price; }
}