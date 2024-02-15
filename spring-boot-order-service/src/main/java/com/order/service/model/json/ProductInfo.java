package com.order.service.model.json;

import lombok.Data;


@Data
public class ProductInfo {

    private int id;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private String status;

    public ProductInfo() {
    }

    public ProductInfo(String name, String description, double price, int quantity, String status) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.status = status;
    }
}
