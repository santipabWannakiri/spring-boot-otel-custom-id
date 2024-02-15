package com.order.service.model.json;

import com.order.service.constants.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "Response")
public class AppResponse {

    private String appResponseCode;
    private String appMessageCode;
    private String description;

    public AppResponse() {
    }

    public AppResponse(String appResponseCode, String appMessageCode, String description) {
        this.appResponseCode = appResponseCode;
        this.appMessageCode = appMessageCode;
        this.description = description;
    }

    public AppResponse(String description) {
        this.appResponseCode = Constants.SUCCESS_CODE;
        this.appMessageCode = Constants.SUCCESS_MESSAGE_CODE;
        this.description = description;
    }
}
