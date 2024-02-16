package com.order.service.controller;

import com.order.service.constants.Constants;
import com.order.service.model.json.AppResponse;
import com.order.service.model.Cart;
import com.order.service.model.json.CartRequest;
import com.order.service.service.CartService;
import com.order.service.util.OpenTelemetryContextUtil;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.semconv.SemanticAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CartController {
    private CartService cartService;
    private final OpenTelemetry openTelemetry;
    private final Tracer tracer;

    @Autowired
    public CartController(CartService cartService, OpenTelemetry openTelemetry) {
        this.cartService = cartService;
        this.openTelemetry = openTelemetry;
        this.tracer = openTelemetry.getTracer(CartController.class.getName(), "0.1.0");
    }

    @GetMapping("/carts")
    public ResponseEntity<?> getCarts() {
        Optional<List<Cart>> result = Optional.ofNullable(cartService.getAllCarts());
        if (result.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(result.get());
        } else {
            return new ResponseEntity<>(Constants.INTERNAL_ERROR_RESPONSE_OBJECT, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/cart")
    public ResponseEntity<?> addProductToCart(@RequestBody CartRequest request) {
        Context context = OpenTelemetryContextUtil.extractContextFromRequest();
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Span addProductSpan = tracer.spanBuilder(methodName).setParent(context).setSpanKind(SpanKind.SERVER).startSpan();
        addProductSpan.setAttribute(SemanticAttributes.HTTP_METHOD, "PUT");
        try (Scope scope = addProductSpan.makeCurrent()) {
            if (cartService.addProduct(request.getProductId(), request.getQuantity(), request.getUsername())) {
                return new ResponseEntity<>(new AppResponse(Constants.ADD_SUCCESS_MESSAGE), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Constants.INTERNAL_ERROR_RESPONSE_OBJECT, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } finally {
            addProductSpan.end();
        }
    }


}
