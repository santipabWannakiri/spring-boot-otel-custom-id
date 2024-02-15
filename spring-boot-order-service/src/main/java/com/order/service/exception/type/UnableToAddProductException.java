package com.order.service.exception.type;

public class UnableToAddProductException extends RuntimeException{
    public UnableToAddProductException(String message) {
        super(message);
    }
}
