package com.order.service.controller;

import com.order.service.constants.Constants;
import com.order.service.model.json.AppResponse;
import com.order.service.model.Cart;
import com.order.service.model.json.CartRequest;
import com.order.service.service.CartService;
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

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
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
        if (cartService.addProduct(request.getProductId(), request.getQuantity(), request.getUsername())) {
            return new ResponseEntity<>(new AppResponse(Constants.ADD_SUCCESS_MESSAGE), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Constants.INTERNAL_ERROR_RESPONSE_OBJECT, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
