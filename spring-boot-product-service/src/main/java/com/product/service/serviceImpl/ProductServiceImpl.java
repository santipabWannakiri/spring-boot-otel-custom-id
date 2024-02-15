package com.product.service.serviceImpl;

import com.product.service.constants.Constants;
import com.product.service.exception.type.ProductNotFoundException;
import com.product.service.exception.type.QuantityExceedException;
import com.product.service.exception.type.UnableToSaveProductException;
import com.product.service.model.Product;
import com.product.service.model.Status;
import com.product.service.repository.ProductRepository;
import com.product.service.service.ProductService;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.propagation.TextMapSetter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> findAll() {
        Optional<List<Product>> result = Optional.ofNullable((List<Product>) productRepository.findByStatus(Status.CURRENT));
        return result.orElseThrow(() -> {
            log.error("Product not found.");
            throw new ProductNotFoundException(Constants.PRODUCT_NOT_FOUND_MESSAGE);
        });
    }

    @Override
    public Product findById(int id) {
        Optional<Product> product = productRepository.findById(id);
        return product.orElseThrow(() -> {
            log.error("Not found product id: " + id);
            throw new ProductNotFoundException(Constants.PRODUCT_NOT_FOUND_MESSAGE);
        });
    }

    @Override
    public Product save(Product product) {
        Optional<Product> result = Optional.of(productRepository.save(product));
        return result.orElseThrow(() -> {
            log.error("Unable to save product.");
            throw new UnableToSaveProductException(Constants.UNABLE_TO_SAVE_PRODUCT_MESSAGE);
        });
    }

    @Override
    public boolean updateProduct(int productId, Product uProduct) {
        Product result = findById(productId);
        result.setName(uProduct.getName());
        result.setDescription(uProduct.getDescription());
        result.setPrice(uProduct.getPrice());
        result.setQuantity(uProduct.getQuantity());
        Optional<Product> updatedProduct = Optional.ofNullable(save(result));
        if (updatedProduct.isPresent()) {
            return true;
        } else {
            log.error("Unable to save product. Product details: {}", result);
            throw new UnableToSaveProductException(Constants.UNABLE_TO_SAVE_PRODUCT_MESSAGE);
        }
    }

    @Override
    public boolean deleteById(int id) {
        Optional<Product> queryResultProduct = productRepository.findById(id);
        Product product = queryResultProduct.orElseThrow(() -> {
            log.error("Not found product id: " + id);
            throw new ProductNotFoundException(Constants.PRODUCT_NOT_FOUND_MESSAGE);
        });
        product.setStatus(Status.DELETE);
        Optional<Product> result = Optional.of(productRepository.save(product));
        if (result.isPresent() && result.get().getStatus().equals(Status.DELETE)) {
            return true;
        } else {
            log.error("Unable to change status product id :" + id + " to inactive.");
            throw new UnableToSaveProductException(Constants.UNABLE_TO_SAVE_PRODUCT_MESSAGE);
        }
    }

    @Override
    public boolean deductQuantity(int productId, int quantity) {
        Optional<Product> queryResultProduct = productRepository.findById(productId);
        Product product = queryResultProduct.orElseThrow(() -> {
            log.error("Not found product id: " + productId);
            throw new ProductNotFoundException(Constants.PRODUCT_NOT_FOUND_MESSAGE);
        });
        if (product.getQuantity() - quantity < 0) {
            log.error("Exceed current product quantity - ID : " + productId);
            throw new QuantityExceedException(Constants.QUANTITY_EXCEED_MESSAGE);
        } else {
            product.setQuantity(product.getQuantity() - quantity);
            Optional<Product> deductQuantity = Optional.ofNullable(save(product));
            if (deductQuantity.isPresent()) {
                return true;
            } else {
                return false;
            }
        }
    }
}
