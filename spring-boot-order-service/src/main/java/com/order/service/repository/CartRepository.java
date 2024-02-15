package com.order.service.repository;

import com.order.service.model.Cart;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("CartRepository")
public interface CartRepository extends CrudRepository<Cart, Integer> {
    @Query("SELECT c FROM Cart c WHERE c.productId = :productId AND c.user.id = :userId")
    Optional<Cart> findByProductIdAndUserId(@Param("productId") int productId, @Param("userId") int userId);
}
