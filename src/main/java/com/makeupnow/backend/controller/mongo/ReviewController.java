package com.makeupnow.backend.controller.mongo;

import com.makeupnow.backend.model.mongo.Review;
import com.makeupnow.backend.repository.mongo.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    // Ajouter un avis
    @PostMapping
    public Review addReview(@RequestBody Review review) {
        return reviewRepository.save(review);
    }

    // Récupérer tous les avis d’un provider
    @GetMapping("/provider/{providerId}")
    public List<Review> getReviewsByProvider(@PathVariable Long providerId) {
        return reviewRepository.findByProviderId(providerId);
    }

    // Récupérer tous les avis d’un customer
    @GetMapping("/customer/{customerId}")
    public List<Review> getReviewsByCustomer(@PathVariable Long customerId) {
        return reviewRepository.findByCustomerId(customerId);
    }

    // Récupérer un avis par ID
    @GetMapping("/{id}")
    public Optional<Review> getReviewById(@PathVariable String id) {
        return reviewRepository.findById(id);
    }
}