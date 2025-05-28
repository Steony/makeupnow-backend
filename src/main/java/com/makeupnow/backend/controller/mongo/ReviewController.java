package com.makeupnow.backend.controller.mongo;

import com.makeupnow.backend.dto.review.ReviewCreateDTO;
import com.makeupnow.backend.dto.review.ReviewUpdateDTO;
import com.makeupnow.backend.dto.review.ReviewResponseDTO;
import com.makeupnow.backend.exception.ResourceNotFoundException;
import com.makeupnow.backend.model.mongo.Review;
import com.makeupnow.backend.repository.mongo.ReviewRepository;
import com.makeupnow.backend.security.SecurityUtils;
import com.makeupnow.backend.service.mongo.ReviewService;
import com.makeupnow.backend.service.mysql.UserActionLogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserActionLogService userActionLogService;

    // ✅ Créer une review - accès Client
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ReviewResponseDTO> createReview(@RequestBody ReviewCreateDTO dto) {
        return ResponseEntity.ok(reviewService.createReview(dto));
    }

     // ✅ Mise à jour d’un avis - uniquement l’auteur (client)
    @PreAuthorize("hasRole('CLIENT')")
    public boolean updateReview(String reviewId, ReviewUpdateDTO dto) {
        Optional<Review> opt = reviewRepository.findById(reviewId);
        if (opt.isPresent()) {
            Review review = opt.get();

            Long currentUserId = SecurityUtils.getCurrentUserId();
            String currentRole = SecurityUtils.getCurrentUserRole();

            if (!"ADMIN".equals(currentRole) && !review.getCustomerId().equals(currentUserId)) {
                throw new AccessDeniedException("Vous n'avez pas le droit de modifier cet avis.");
            }

            review.setRating(dto.getRating());
            review.setComment(dto.getComment());
            reviewRepository.save(review);

            userActionLogService.logActionByUserId(
                    review.getCustomerId(),
                    "Modification d'avis",
                    "Avis modifié par " + review.getCustomerName() +
                            " pour le prestataire " + review.getProviderName() +
                            ", nouvelle note : " + dto.getRating()
            );

            return true;
        }
        return false;
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
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
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
    @GetMapping("/all")
@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReviewResponseDTO>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }
}
