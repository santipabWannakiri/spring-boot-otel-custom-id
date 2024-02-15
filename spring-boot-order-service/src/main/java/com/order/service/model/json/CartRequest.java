package com.order.service.model.json;

import lombok.Data;

@Data
public class CartRequest {

    private int productId;
    private String username;
    private int quantity;

    public CartRequest() {
    }

    public CartRequest(int productId, String username, int quantity) {
        this.productId = productId;
        this.username = username;
        this.quantity = quantity;
    }
}
