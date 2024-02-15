package com.order.service.constants;

import com.order.service.model.json.AppResponse;

public final class Constants {
    //====== SUCCESS ======
    public static final String SUCCESS_CODE = "0000";
    public static final String SUCCESS_MESSAGE_CODE = "SUCCESS";
    public static final String ADD_SUCCESS_MESSAGE = "Added product to cart.";

    //====== INTERNAL_SERVER_ERROR ======
    public static final String INTERNAL_ERROR_CODE = "6601";
    public static final String INTERNAL_MESSAGE_CODE = "FAILURE";
    public static final String UNABLE_REGISTER_MESSAGE = "Unable to register. Please check your connection and try again.";
    public static final String UNABLE_TO_PROCESS_MESSAGE = "Unable to process request. Please try again.";

    //====== USER_NOT_FOUND ======
    public static final String USER_NOT_FOUND_ERROR_CODE = "6600";
    public static final String USER_NOT_FOUND_MESSAGE_CODE = "USER_NOT_FOUND";
    public static final String USER_NOT_FOUND_MESSAGE = "User not found.";

    //====== QUANTITY_EXCEED_ERROR ======
    public static final String QUANTITY_EXCEED_ERROR_CODE = "6601";
    public static final String QUANTITY_EXCEED_MESSAGE_CODE = "QUANTITY_EXCEED";
    public static final String QUANTITY_EXCEED_MESSAGE = "User provide product quantity exceed existing";


    //====== PRODUCT_STATUS_NOT_ACTIVE_ERROR ======
    public static final String PRODUCT_STATUS_NOT_ACTIVE_ERROR_CODE = "6601";
    public static final String PRODUCT_STATUS_NOT_ACTIVE_MESSAGE_CODE = "PRODUCT_NOT_ACTIVE";
    public static final String PRODUCT_STATUS_NOT_ACTIVE_MESSAGE = "The product status inactive.";

    //====== UNABLE_CONNECT_TO_ENDPOINT_ERROR ======
    public static final String UNABLE_CONNECT_TO_ENDPOINT_ERROR_CODE = "6601";
    public static final String UNABLE_CONNECT_TO_ENDPOINT_MESSAGE_CODE = "UNABLE_CONNECT_TO_ENDPOINT";
    public static final String UNABLE_CONNECT_TO_ENDPOINT_MESSAGE = "Unable to reach out to target endpoint";
    public static final String UNABLE_TO_DEDUCT_QUANTITY_MESSAGE = "Unable to deduct product quantity";

    //====== UNABLE_TO_SAVE_PRODUCT_ERROR ======
    public static final String UNABLE_TO_SAVE_PRODUCT_ERROR_CODE = "6601";
    public static final String UNABLE_TO_SAVE_PRODUCT_MESSAGE_CODE = "UNABLE_TO_SAVE_PRODUCT";
    public static final String UNABLE_TO_SAVE_PRODUCT_MESSAGE = "There is the issue while saving product.";

    //====== INTERNAL_ERROR_RESPONSE_OBJECT ======
    public static final AppResponse INTERNAL_ERROR_RESPONSE_OBJECT = new AppResponse(Constants.INTERNAL_ERROR_CODE, Constants.INTERNAL_MESSAGE_CODE, Constants.UNABLE_TO_PROCESS_MESSAGE);

    //====== Interceptor parameter ======
    public static final String X_TRACE_ID = "traceId";
    public static final String X_REQUEST_ID = "requestId";
}