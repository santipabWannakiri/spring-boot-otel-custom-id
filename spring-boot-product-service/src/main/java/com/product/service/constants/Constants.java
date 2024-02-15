package com.product.service.constants;

import com.product.service.model.json.AppResponse;

public final class Constants {
    //====== SUCCESS ======
    public static final String SUCCESS_CODE = "0000";
    public static final String SUCCESS_MESSAGE_CODE = "SUCCESS";
    public static final String DELETE_SUCCESS_MESSAGE = "Deleted the product.";
    public static final String UPDATE_SUCCESS_MESSAGE = "Updated product.";
    public static final String DEDUCT_SUCCESS_MESSAGE = "Deducted the quantity product.";

    //====== INTERNAL_SERVER_ERROR ======
    public static final String INTERNAL_ERROR_CODE = "6601";
    public static final String INTERNAL_MESSAGE_CODE = "FAILURE";
   public static final String UNABLE_TO_PROCESS_MESSAGE = "Unable to process request. Please try again.";

    //====== PRODUCT_NOT_FOUND ======
    public static final String PRODUCT_NOT_FOUND_ERROR_CODE = "6600";
    public static final String PRODUCT_NOT_FOUND_MESSAGE_CODE = "PRODUCT_NOT_FOUND";
    public static final String PRODUCT_NOT_FOUND_MESSAGE = "Product not found.";


    //====== UNABLE_TO_SAVE_PRODUCT_ERROR ======
    public static final String UNABLE_TO_SAVE_PRODUCT_ERROR_CODE = "6601";
    public static final String UNABLE_TO_SAVE_PRODUCT_MESSAGE_CODE = "UNABLE_TO_SAVE_PRODUCT";
    public static final String UNABLE_TO_SAVE_PRODUCT_MESSAGE = "There is the issue while saving product.";


    //====== QUANTITY_EXCEED_ERROR ======
    public static final String QUANTITY_EXCEED_ERROR_CODE = "6601";
    public static final String QUANTITY_EXCEED_MESSAGE_CODE = "QUANTITY_EXCEED";
    public static final String QUANTITY_EXCEED_MESSAGE = "Exceed current product quantity.";

    //====== INTERNAL_ERROR_RESPONSE_OBJECT ======
    public static final AppResponse INTERNAL_ERROR_RESPONSE_OBJECT = new AppResponse(Constants.INTERNAL_ERROR_CODE, Constants.INTERNAL_MESSAGE_CODE, Constants.UNABLE_TO_PROCESS_MESSAGE);

    public static final String X_TRACE_ID = "traceId";
    public static final String X_REQUEST_ID = "requestId";
}
