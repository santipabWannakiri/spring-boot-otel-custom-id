package com.order.service.exception.type;

public class UnableToConnectToEndpointException extends RuntimeException {
    public UnableToConnectToEndpointException(String message) {
        super(message);
    }
}
