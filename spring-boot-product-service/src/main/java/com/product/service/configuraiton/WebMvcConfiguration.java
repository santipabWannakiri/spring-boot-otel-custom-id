package com.product.service.configuraiton;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    private final InterceptorConfiguration interceptorConfiguration;

    public WebMvcConfiguration(InterceptorConfiguration interceptorConfiguration) {
        this.interceptorConfiguration = interceptorConfiguration;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptorConfiguration);
    }
}
