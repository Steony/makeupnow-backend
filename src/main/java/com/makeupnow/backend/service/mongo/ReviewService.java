package com.makeupnow.backend.service.mongo;

import com.makeupnow.backend.dto.review.ReviewCreateDTO;
import com.makeupnow.backend.dto.review.ReviewUpdateDTO;
import com.makeupnow.backend.dto.review.ReviewResponseDTO;
import com.makeupnow.backend.exception.ResourceNotFoundException;
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
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserActionLogService userActionLogService;

    @PreAuthorize("hasRole('CLIENT')")
    public ReviewResponseDTO createReview(ReviewCreateDTO dto) {
        Review review = new Review();
        review.setCustomerId(dto.getCustomerId());
        review.setProviderId(dto.getProviderId());
        review.setMakeupServiceId(dto.getMakeupServiceId()); // ✅ nouveau champ
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setDateComment(LocalDateTime.now());

        userRepository.findById(dto.getCustomerId()).ifPresent(user ->
                review.setCustomerName(user.getFirstname() + " " + user.getLastname()));
        userRepository.findById(dto.getProviderId()).ifPresent(user ->
                review.setProviderName(user.getFirstname() + " " + user.getLastname()));

        Review saved = reviewRepository.save(review);

        userActionLogService.logActionByUserId(
                review.getCustomerId(),
                "Création d'avis",
                "Avis créé pour le provider ID " + review.getProviderId() +
                " et service ID " + review.getMakeupServiceId() +
                " avec note " + review.getRating()
        );

        return mapToDTO(saved);
    }

    @PreAuthorize("hasRole('CLIENT')")
    public boolean updateReview(String reviewId, ReviewUpdateDTO dto) {
        Optional<Review> opt = reviewRepository.findById(reviewId);
        if (opt.isPresent()) {
            Review review = opt.get();
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

    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteReview(Long adminId, String reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review non trouvée."));
        reviewRepository.deleteById(reviewId);

        userActionLogService.logActionByUserId(
                adminId,
                "Suppression d'avis",
                "Avis supprimé pour prestataire " + review.getProviderName() +
                        " laissé par " + review.getCustomerName()
        );
        return true;
    }

    @PreAuthorize("isAuthenticated()")
    public List<ReviewResponseDTO> getReviewsByProvider(Long providerId) {
        return reviewRepository.findByProviderId(providerId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public List<ReviewResponseDTO> getReviewsByCustomer(Long customerId) {
        return reviewRepository.findByCustomerId(customerId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }


@PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
public List<ReviewResponseDTO> getReviewsByMakeupService(Long serviceId) {
    return reviewRepository.findByMakeupServiceId(serviceId)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
}


    @PreAuthorize("hasRole('ADMIN')")
    public List<ReviewResponseDTO> getAllReviews() {
        return reviewRepository.findAll()
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private ReviewResponseDTO mapToDTO(Review review) {
        return ReviewResponseDTO.builder()
                .id(review.getId())
                .customerId(review.getCustomerId())
                .customerName(review.getCustomerName())
                .providerId(review.getProviderId())
                .providerName(review.getProviderName())
                .makeupServiceId(review.getMakeupServiceId()) // ✅ changé ici aussi
                .rating(review.getRating())
                .comment(review.getComment())
                .dateComment(review.getDateComment())
                .build();
    }
}
