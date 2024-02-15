package com.order.service.service;

import com.order.service.model.Cart;
import com.order.service.model.User;

import java.util.List;

public interface CartService {


     boolean addProduct(int productId ,int quantity , String userName);

     List<Cart> getAllCarts();
}
