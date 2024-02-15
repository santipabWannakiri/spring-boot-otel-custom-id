package com.product.service.controller;

import com.product.service.constants.Constants;
import com.product.service.model.Product;
import com.product.service.model.json.AppResponse;
import com.product.service.model.json.QuantityRequest;
import com.product.service.service.ProductService;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapSetter;
import io.opentelemetry.semconv.SemanticAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ProductController {

    private ProductService productService;
    private final OpenTelemetry openTelemetry;
    private final Tracer tracer;
    private final TextMapSetter<HttpHeaders> setter;
    private static final ThreadLocal<Context> parentContexthreadLocal = new ThreadLocal<>();

    public static void setContext(Context context) {
        parentContexthreadLocal.set(context);
    }

    @Autowired
    public ProductController(ProductService productService, OpenTelemetry openTelemetry, TextMapSetter<HttpHeaders> setter) {
        this.productService = productService;
        this.openTelemetry = openTelemetry;
        this.tracer = openTelemetry.getTracer(ProductController.class.getName(), "0.1.0");
        this.setter = setter;
    }

    @GetMapping("/products")
    public ResponseEntity<?> getAllProducts() {
        Optional<List<Product>> result = Optional.ofNullable(productService.findAll());
        if (result.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(result.get());
        } else {
            return new ResponseEntity<>(Constants.INTERNAL_ERROR_RESPONSE_OBJECT, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") int id) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Span getProductSpan = tracer.spanBuilder(methodName).setParent(parentContexthreadLocal.get()).setSpanKind(SpanKind.SERVER).startSpan();
        getProductSpan.setAttribute(SemanticAttributes.HTTP_METHOD, "GET");
        try (Scope scope = getProductSpan.makeCurrent()) {
            Optional<Product> result = Optional.ofNullable(productService.findById(id));
            if (result.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(result.get());
            } else {
                return new ResponseEntity<>(Constants.INTERNAL_ERROR_RESPONSE_OBJECT, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } finally {
            getProductSpan.end();
        }

    }

    @PostMapping("/products")
    public ResponseEntity<?> createNewProduct(@RequestBody Product product) {
        Optional<Product> result = Optional.ofNullable(productService.save(product));
        if (result.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(result.get());
        } else {
            return new ResponseEntity<>(Constants.INTERNAL_ERROR_RESPONSE_OBJECT, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/product/{PID}")
    public ResponseEntity<?> updateProduct(@PathVariable("PID") int productId, @RequestBody Product uProduct) {
        if (productService.updateProduct(productId, uProduct)) {
            return new ResponseEntity<>(new AppResponse(Constants.UPDATE_SUCCESS_MESSAGE), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Constants.INTERNAL_ERROR_RESPONSE_OBJECT, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/product/{PID}")
    public ResponseEntity<?> deleteProduct(@PathVariable("PID") int id) {
        if (productService.deleteById(id)) {
            return new ResponseEntity<>(new AppResponse(Constants.DELETE_SUCCESS_MESSAGE), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Constants.INTERNAL_ERROR_RESPONSE_OBJECT, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/product/deduct/{PID}")
    public ResponseEntity<?> deductQuantity(@PathVariable("PID") int productId, @RequestBody QuantityRequest quantity) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Span deductSpan = tracer.spanBuilder(methodName).setParent(parentContexthreadLocal.get()).setSpanKind(SpanKind.SERVER).startSpan();
        deductSpan.setAttribute(SemanticAttributes.HTTP_METHOD, "PUT");
        try (Scope scope = deductSpan.makeCurrent()) {
            if (productService.deductQuantity(productId, quantity.getRequestedQuantity())) {
                return new ResponseEntity<>(new AppResponse(Constants.DEDUCT_SUCCESS_MESSAGE), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Constants.INTERNAL_ERROR_RESPONSE_OBJECT, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } finally {
            deductSpan.end();
        }
    }

}
