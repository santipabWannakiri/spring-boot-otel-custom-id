package com.product.service;

import com.product.service.model.Product;
import com.product.service.model.Status;
import com.product.service.service.ProductService;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringBootProductServiceApplication {

    private ProductService productService;

    public SpringBootProductServiceApplication(ProductService productService) {
        this.productService = productService;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootProductServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(String[] args) {
        return r -> {
            createInstantObject();
        };
    }

    private void createInstantObject() {
        Product product1 = new Product("THAI TEA", "Drinking from Thailand", 5.00, 99, Status.CURRENT);
        Product product2 = new Product("HONHTHAI", "Compound herb from thailand", 3.25, 99, Status.CURRENT);
        Product product3 = new Product("VFOODS MIX", "Biscuit sticks original flavor", 2.50, 50, Status.CURRENT);
        productService.save(product1);
        productService.save(product2);
        productService.save(product3);
    }

}
