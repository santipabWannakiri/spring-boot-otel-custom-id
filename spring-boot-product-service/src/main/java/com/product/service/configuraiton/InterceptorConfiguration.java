package com.product.service.configuraiton;

import com.product.service.constants.Constants;
import com.product.service.controller.ProductController;
import com.product.service.serviceImpl.ProductServiceImpl;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Configuration
public class InterceptorConfiguration implements HandlerInterceptor {

    private final OpenTelemetry openTelemetry;

    private final Tracer tracer;

    @Autowired
    public InterceptorConfiguration(OpenTelemetry openTelemetry) {
        this.openTelemetry = openTelemetry;
        this.tracer = openTelemetry.getTracer(InterceptorConfiguration.class.getName(), "0.1.0");
    }


    TextMapGetter<HttpServletRequest> getter = new TextMapGetter<HttpServletRequest>() {
        @Override
        public Iterable<String> keys(HttpServletRequest carrier) {
            return Collections.list(carrier.getHeaderNames());
        }

        @Override
        public String get(HttpServletRequest carrier, String key) {
            return carrier.getHeader(key);
        }
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Optional<Context> extractedContext = Optional.ofNullable(openTelemetry.getPropagators().getTextMapPropagator().extract(Context.current(), request, getter));
        if (extractedContext.isPresent()) {
            Span propagationParent = tracer.spanBuilder("interceptor").setSpanKind(SpanKind.SERVER).setParent(extractedContext.get()).startSpan();
            SpanContext spanContext = propagationParent.getSpanContext();
            String traceId = spanContext != null ? spanContext.getTraceId() : "Unable get traceId";
            System.out.println("trace idd : " + traceId);
            request.setAttribute(Constants.X_TRACE_ID, traceId);
            request.setAttribute(Constants.X_REQUEST_ID, UUID.randomUUID().toString());
            ProductController.setContext(extractedContext.get());
            propagationParent.end();
        } else {
            request.setAttribute(Constants.X_TRACE_ID, "Unable get traceId");
            request.setAttribute(Constants.X_REQUEST_ID, UUID.randomUUID().toString());
        }
        return true;
    }


}