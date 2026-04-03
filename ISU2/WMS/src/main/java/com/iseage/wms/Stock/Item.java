package com.iseage.wms.Stock;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name="items")
public class Item {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true, nullable=false)
    @NotBlank(message = "Item name cannot be blank")
    private String name;

    @Column(nullable=false)
    @Min(value = 0, message = "Quantity cannot be negative")
    private int quantity;

    @Column(nullable=false)
    @Min(value = 0, message = "Price cannot be negative")
    private double price;

    @Column(nullable=false)
    @NotBlank(message = "Description is required")
    private String description;

    public Item() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}