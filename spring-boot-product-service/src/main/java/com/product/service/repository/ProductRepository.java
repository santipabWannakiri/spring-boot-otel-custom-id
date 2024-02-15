package com.product.service.repository;

import com.product.service.model.Product;
import com.product.service.model.Status;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ProductRepository")
public interface ProductRepository extends CrudRepository<Product,Integer> {
    List<Product> findByStatus(Status status);

}
