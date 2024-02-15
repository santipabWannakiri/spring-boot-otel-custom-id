package com.order.service.exception.type;

public class ProductStatusNotActiveException extends RuntimeException{
    public ProductStatusNotActiveException(String message) {
        super(message);
    }
}
