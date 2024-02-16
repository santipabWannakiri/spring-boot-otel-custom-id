package com.order.service.configuration;


import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static io.opentelemetry.semconv.ResourceAttributes.SERVICE_NAME;
import static io.opentelemetry.semconv.ResourceAttributes.SERVICE_VERSION;

@Configuration
public class OtelConfiguration {
    @Bean
    public OpenTelemetry openTelemetry() {
        Resource resource = Resource.getDefault().toBuilder().put(SERVICE_NAME, "order-service").put(SERVICE_VERSION, "0.1.0").build();

        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(OtlpGrpcSpanExporter.builder().build()).build())
                .setResource(resource)
                .build();

        SdkMeterProvider sdkMeterProvider = SdkMeterProvider.builder()
                .registerMetricReader(PeriodicMetricReader.builder(OtlpGrpcMetricExporter.builder().build()).build())
                .setResource(resource)
                .build();

        SdkLoggerProvider sdkLoggerProvider = SdkLoggerProvider.builder()
                .addLogRecordProcessor(
                        BatchLogRecordProcessor.builder(OtlpGrpcLogRecordExporter.builder().build()).build())
                .setResource(resource)
                .build();

        OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
                .setTracerProvider(sdkTracerProvider)
                .setMeterProvider(sdkMeterProvider)
                .setLoggerProvider(sdkLoggerProvider)
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .buildAndRegisterGlobal();

        return openTelemetry;
    }

    @Bean
    public TextMapSetter<HttpHeaders> setterTextMap() {
        return new TextMapSetter<HttpHeaders>() {
            @Override
            public void set(HttpHeaders carrier, String key, String value) {
                carrier.set(key, value);
            }
        };
    }

    @Bean
    public TextMapGetter<HttpHeaders> getterTextMap() {
        return new TextMapGetter<HttpHeaders>() {
            @Override
            public Iterable<String> keys(HttpHeaders carrier) {
                return carrier.keySet();
            }
            @Override
            public String get(HttpHeaders carrier, String key) {
                return carrier.getFirst(key);
            }
        };
    }


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
