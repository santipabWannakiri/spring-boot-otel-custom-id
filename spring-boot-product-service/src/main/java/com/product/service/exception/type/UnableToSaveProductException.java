package com.product.service.exception.type;

public class UnableToSaveProductException extends RuntimeException{
    public UnableToSaveProductException(String message) {
        super(message);
    }
}
