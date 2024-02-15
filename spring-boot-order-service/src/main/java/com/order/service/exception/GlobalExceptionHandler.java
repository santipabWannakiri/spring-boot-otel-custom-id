package com.order.service.exception;

import com.order.service.constants.Constants;
import com.order.service.exception.type.*;
import com.order.service.model.json.AppResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<AppResponse> handlerException(Exception ex) {
        AppResponse response = new AppResponse();
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        if (ex instanceof UserNotFoundException) {
            response.setAppResponseCode(Constants.USER_NOT_FOUND_ERROR_CODE);
            response.setAppMessageCode(Constants.USER_NOT_FOUND_MESSAGE_CODE);
            response.setDescription(ex.getMessage());
            httpStatus = HttpStatus.NOT_FOUND;
        } else if (ex instanceof QuantityExceedException) {
            response.setAppResponseCode(Constants.QUANTITY_EXCEED_ERROR_CODE);
            response.setAppMessageCode(Constants.QUANTITY_EXCEED_MESSAGE_CODE);
            response.setDescription(ex.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (ex instanceof ProductStatusNotActiveException) {
            response.setAppResponseCode(Constants.PRODUCT_STATUS_NOT_ACTIVE_ERROR_CODE);
            response.setAppMessageCode(Constants.PRODUCT_STATUS_NOT_ACTIVE_MESSAGE_CODE);
            response.setDescription(ex.getMessage());
            httpStatus = HttpStatus.FORBIDDEN;
        } else if (ex instanceof UnableToConnectToEndpointException) {
            response.setAppResponseCode(Constants.UNABLE_CONNECT_TO_ENDPOINT_ERROR_CODE);
            response.setAppMessageCode(Constants.UNABLE_CONNECT_TO_ENDPOINT_MESSAGE_CODE);
            response.setDescription(ex.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        } else if (ex instanceof UnableToAddProductException) {
            response.setAppResponseCode(Constants.UNABLE_TO_SAVE_PRODUCT_ERROR_CODE);
            response.setAppMessageCode(Constants.UNABLE_TO_SAVE_PRODUCT_MESSAGE_CODE);
            response.setDescription(ex.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(response, httpStatus);
    }

}
