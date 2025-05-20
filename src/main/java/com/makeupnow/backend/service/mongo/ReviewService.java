package com.makeupnow.backend.service.mongo;

import com.makeupnow.backend.model.mongo.Review;
import com.makeupnow.backend.repository.mongo.ReviewRepository;
import com.makeupnow.backend.repository.mysql.UserRepository;
import com.makeupnow.backend.service.mysql.UserActionLogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserActionLogService userActionLogService;

    @Autowired
    private UserRepository userRepository;

     // Créer une review
    public Review createReview(Review review) {
    review.setDateComment(LocalDateTime.now());

    userRepository.findById(review.getCustomerId()).ifPresent(user -> {
        review.setCustomerName(user.getFirstname() + " " + user.getLastname());
    });
    userRepository.findById(review.getProviderId()).ifPresent(user -> {
        review.setProviderName(user.getFirstname() + " " + user.getLastname());
    });

    Review savedReview = reviewRepository.save(review);

    // Log création review
    userActionLogService.logActionByUserId(
        review.getCustomerId(),
        "Création d'avis",
        "Avis créé pour le provider ID " + review.getProviderId() + " avec note " + review.getRating()
    );

    return savedReview;
}


    // Mettre à jour une review (accessible client)
    @PreAuthorize("hasRole('CLIENT')")
    public boolean updateReview(String reviewId, int rating, String comment) {
        Optional<Review> optReview = reviewRepository.findById(reviewId);
        if (optReview.isPresent()) {
            Review review = optReview.get();
            review.setRating(rating);
            review.setComment(comment);
            reviewRepository.save(review);

            // Log modification review avec noms complets
            userActionLogService.logActionByUserId(
                review.getCustomerId(),
                "Modification d'avis",
                "Avis modifié par " + review.getCustomerName() +
                " pour le prestataire " + review.getProviderName() + 
                ", nouvelle note : " + rating
            );

            return true;
        }
        return false;
    }
    // Supprimer une review (accessible admin)
    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteReview(Long adminId, String reviewId) {
        if (reviewRepository.existsById(reviewId)) {
            Review review = reviewRepository.findById(reviewId).get();

            reviewRepository.deleteById(reviewId);

            // Log suppression review avec noms complets
            userActionLogService.logActionByUserId(
                adminId,
                "Suppression d'avis",
                "Avis supprimé par admin ID " + adminId +
                " pour le prestataire " + review.getProviderName() +
                " laissé par " + review.getCustomerName()
            );

            return true;
        }
        return false;
    }

    // Lister les reviews par prestataire (accessible par tous)
    @PreAuthorize("isAuthenticated()")
    public List<Review> getReviewsByProvider(Long providerId) {
        return reviewRepository.findByProviderId(providerId);
    }

    // Lister les reviews par client (accessible client et admin)
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public List<Review> getReviewsByCustomer(Long customerId) {
        return reviewRepository.findByCustomerId(customerId);
    }

    // Lister toutes les reviews (accessible admin)
    @PreAuthorize("hasRole('ADMIN')")
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
}
