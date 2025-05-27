package com.makeupnow.backend.controller.mongo;

import com.makeupnow.backend.dto.review.ReviewCreateDTO;
import com.makeupnow.backend.dto.review.ReviewUpdateDTO;
import com.makeupnow.backend.dto.review.ReviewResponseDTO;
import com.makeupnow.backend.exception.ResourceNotFoundException;
import com.makeupnow.backend.service.mongo.ReviewService;

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

    // ✅ Créer une review - accès Client
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ReviewResponseDTO> createReview(@RequestBody ReviewCreateDTO dto) {
        return ResponseEntity.ok(reviewService.createReview(dto));
    }

    // ✅ Mettre à jour une review - accès Client
    @PutMapping("/{reviewId}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<String> updateReview(@PathVariable String reviewId,
                                               @RequestBody ReviewUpdateDTO dto) {
        boolean updated = reviewService.updateReview(reviewId, dto);
        if (updated) {
            return ResponseEntity.ok("Review mise à jour avec succès.");
        } else {
            throw new ResourceNotFoundException("Review non trouvée.");
        }
    }

    // ✅ Supprimer une review - accès Admin
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteReview(@PathVariable String reviewId,
                                               @RequestParam Long adminId) {
        boolean deleted = reviewService.deleteReview(adminId, reviewId);
        if (deleted) {
            return ResponseEntity.ok("Review supprimée.");
        } else {
            throw new ResourceNotFoundException("Review non trouvée.");
        }
    }

    // ✅ Lister les reviews par prestataire - accès Client
    @GetMapping("/provider/{providerId}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByProvider(@PathVariable Long providerId) {
        return ResponseEntity.ok(reviewService.getReviewsByProvider(providerId));
    }

    // ✅ Lister les reviews par client - accès Client/Admin
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(reviewService.getReviewsByCustomer(customerId));
    }

    // ✅ Lister les reviews par prestation (MakeupService) - accès Tous
@GetMapping("/service/{serviceId}")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<List<ReviewResponseDTO>> getReviewsByMakeupService(@PathVariable Long serviceId) {
    return ResponseEntity.ok(reviewService.getReviewsByMakeupService(serviceId));
}


    // ✅ Lister toutes les reviews - accès Admin
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReviewResponseDTO>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }
}
