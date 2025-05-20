package com.makeupnow.backend.controller.mongo;

import com.makeupnow.backend.model.mongo.Review;
import com.makeupnow.backend.service.mongo.ReviewService;
import com.makeupnow.backend.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // Créer une review - accès Client
    @PostMapping("/")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        return ResponseEntity.ok(reviewService.createReview(review));
    }

    // Mettre à jour une review - accès Client
    @PutMapping("/{reviewId}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<String> updateReview(@PathVariable String reviewId,
                                               @RequestParam int rating,
                                               @RequestParam String comment) {
        boolean updated = reviewService.updateReview(reviewId, rating, comment);
        if (updated) {
            return ResponseEntity.ok("Review mise à jour avec succès.");
        } else {
            throw new ResourceNotFoundException("Review non trouvée.");
        }
    }

    // Supprimer une review - accès Admin
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteReview(@PathVariable String reviewId, @RequestParam Long adminId) {
        boolean deleted = reviewService.deleteReview(adminId, reviewId);
        if (deleted) {
            return ResponseEntity.ok("Review supprimée.");
        } else {
            throw new ResourceNotFoundException("Review non trouvée.");
        }
    }

    // Lister les reviews par provider - accès Client
    @GetMapping("/provider/{providerId}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<Review>> getReviewsByProvider(@PathVariable Long providerId) {
        return ResponseEntity.ok(reviewService.getReviewsByProvider(providerId));
    }

    // Lister les reviews par customer - accès Client
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<Review>> getReviewsByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(reviewService.getReviewsByCustomer(customerId));
    }

    // Lister toutes les reviews - accès Admin
    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }
}
