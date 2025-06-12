package com.makeupnow.backend.service.mongo;

import com.makeupnow.backend.dto.review.ReviewCreateDTO;
import com.makeupnow.backend.dto.review.ReviewUpdateDTO;
import com.makeupnow.backend.dto.review.ReviewResponseDTO;
import com.makeupnow.backend.exception.ResourceNotFoundException;
import com.makeupnow.backend.model.mongo.Review;
import com.makeupnow.backend.model.mysql.MakeupService;
import com.makeupnow.backend.repository.mongo.ReviewRepository;
import com.makeupnow.backend.repository.mysql.MakeupServiceRepository;
import com.makeupnow.backend.repository.mysql.UserRepository;
import com.makeupnow.backend.security.SecurityUtils;
import com.makeupnow.backend.service.mysql.UserActionLogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserActionLogService userActionLogService;

    @Autowired
    private MakeupServiceRepository makeupServiceRepository;

    @PreAuthorize("hasRole('CLIENT')")
public ReviewResponseDTO createReview(ReviewCreateDTO dto) {
    Long currentUserId = SecurityUtils.getCurrentUserId();
    if (!dto.getCustomerId().equals(currentUserId)) {
        throw new AccessDeniedException("Vous ne pouvez créer un avis que pour votre propre compte.");
    }

    Review review = new Review();
    review.setCustomerId(dto.getCustomerId());
    review.setProviderId(dto.getProviderId());
    review.setMakeupServiceId(dto.getMakeupServiceId());
    review.setBookingId(dto.getBookingId()); // <--- AJOUTÉ ICI
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
            "Avis créé pour le prestataire ID " + review.getProviderId() +
                    " et service ID " + review.getMakeupServiceId() +
                    " (booking ID " + review.getBookingId() + ")" +
                    " avec note " + review.getRating()
    );

    return mapToDTO(saved);
}


    @PreAuthorize("hasRole('CLIENT')")
    public boolean updateReview(String reviewId, ReviewUpdateDTO dto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Avis non trouvé."));

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

    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteReview(Long adminId, String reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Avis non trouvé."));
        reviewRepository.deleteById(reviewId);

        userActionLogService.logActionByUserId(
                adminId,
                "Suppression d'avis",
                "Avis supprimé pour prestataire " + review.getProviderName() +
                        " laissé par " + review.getCustomerName()
        );
        return true;
    }

    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public List<ReviewResponseDTO> getReviewsByProvider(Long providerId) {
        return reviewRepository.findByProviderId(providerId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public List<ReviewResponseDTO> getReviewsByCustomer(Long customerId) {
        return reviewRepository.findByCustomerId(customerId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

   @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
    public List<ReviewResponseDTO> getReviewsByMakeupService(Long serviceId) {
        MakeupService service = makeupServiceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Prestation introuvable."));

        Long currentUserId = SecurityUtils.getCurrentUserId();
        String currentRole = SecurityUtils.getCurrentUserRole();

        if ("PROVIDER".equals(currentRole) && !service.getProvider().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Accès interdit à cette prestation.");
        }

        return reviewRepository.findByMakeupServiceId(serviceId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
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
            .makeupServiceId(review.getMakeupServiceId())
            .bookingId(review.getBookingId()) // <--- AJOUT
            .rating(review.getRating())
            .comment(review.getComment())
            .dateComment(review.getDateComment())
            .build();
}

}
