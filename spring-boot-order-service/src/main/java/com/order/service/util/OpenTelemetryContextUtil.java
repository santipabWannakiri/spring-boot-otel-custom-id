package com.order.service.util;

import com.order.service.constants.Constants;
import io.opentelemetry.context.Context;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class OpenTelemetryContextUtil {
    public static Context extractContextFromRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return (Context) request.getAttribute(Constants.X_OTEL_CONTEXT);
        }
        return Context.current();
    }
}
