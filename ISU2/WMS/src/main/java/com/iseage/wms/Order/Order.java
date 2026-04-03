package com.iseage.wms.Order;

import java.time.LocalDateTime;

public class Order{
    private Long order_id;
    private Long user_id;
    private double total_price;
    private String order_status;
    private String barcode;
    private LocalDateTime created_at;

    public Order() {}

    public Order(Long user_id, String barcode) {
        this.user_id = user_id;
        this.barcode = barcode;
    }

    public Long getOrder_id() { return order_id; }
    public void setOrder_id(Long order_id) { this.order_id = order_id; }
    public Long getUser_id() { return user_id; }
    public void setUser_id(Long user_id) { this.user_id = user_id; }
    public double getTotal_price() { return total_price; }
    public void setTotal_price(double total_price) { this.total_price = total_price; }
    public String getOrder_status() { return order_status; }
    public void setOrder_status(String order_status) { this.order_status = order_status; }
    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }
}