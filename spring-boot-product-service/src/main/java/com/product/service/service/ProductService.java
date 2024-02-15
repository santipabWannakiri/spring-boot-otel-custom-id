package com.product.service.service;

import com.product.service.model.Product;

import java.util.List;

public interface ProductService {

    public List<Product> findAll();

    public Product findById(int id);

    public Product save(Product newProduct);

    public boolean deleteById(int id);

    public boolean updateProduct(int productId, Product uProduct);

    public boolean deductQuantity(int productId, int quantity);

}
