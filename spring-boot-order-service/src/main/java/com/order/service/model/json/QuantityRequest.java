package com.order.service.model.json;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "Quantity")
public class QuantityRequest {

    private int requestedQuantity;

    public QuantityRequest(int quantity) {
        this.requestedQuantity = quantity;
    }

    public QuantityRequest() {
    }
}
