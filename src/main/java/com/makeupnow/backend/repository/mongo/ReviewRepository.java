package com.makeupnow.backend.repository.mongo;

import com.makeupnow.backend.model.mongo.Review;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByProviderId(Long providerId);
    List<Review> findByCustomerId(Long customerId);

}
