package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.dto.makeupservice.MakeupServiceResponseDTO;
import com.makeupnow.backend.dto.provider.ProviderResponseDTO;
import com.makeupnow.backend.dto.provider.ProviderDetailResponseDTO;
import com.makeupnow.backend.dto.schedule.ScheduleResponseDTO;
import com.makeupnow.backend.dto.review.ReviewResponseDTO;
import com.makeupnow.backend.model.mongo.Review;
import com.makeupnow.backend.model.mysql.Provider;
import com.makeupnow.backend.repository.mongo.ReviewRepository;
import com.makeupnow.backend.repository.mysql.ProviderRepository;
import com.makeupnow.backend.repository.mysql.ScheduleRepository;
import com.makeupnow.backend.security.SecurityUtils;
import com.makeupnow.backend.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

@Service
public class ProviderService {

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    // DTO Light pour liste (ex: customer/home)
    public ProviderResponseDTO mapToDTO(Provider provider) {
        String categoriesString = provider.getServices() != null
            ? provider.getServices().stream()
                .map(service -> service.getCategory() != null ? service.getCategory().getTitle() : "")
                .filter(title -> !title.isEmpty())
                .distinct()
                .reduce((a, b) -> a + ", " + b)
                .orElse("")
            : "";

        return ProviderResponseDTO.builder()
                .id(provider.getId())
                .firstname(provider.getFirstname())
                .lastname(provider.getLastname())
                .address(provider.getAddress())
                .averageRating(getAverageRating(provider.getId()))
                .categoriesString(categoriesString)
                .build();
    }

    // DTO détaillé pour la fiche complète (ProviderProfileScreen)
    public ProviderDetailResponseDTO mapToDetailDTO(Provider provider) {
        String categoriesString = provider.getServices() != null
            ? provider.getServices().stream()
                .map(service -> service.getCategory() != null ? service.getCategory().getTitle() : "")
                .filter(title -> !title.isEmpty())
                .distinct()
                .reduce((a, b) -> a + ", " + b)
                .orElse("")
            : "";

        // Map services
        List<MakeupServiceResponseDTO> services = provider.getServices() != null
            ? provider.getServices().stream()
                .map(service -> {
                    MakeupServiceResponseDTO dto = new MakeupServiceResponseDTO();
                    dto.setId(service.getId());
                    dto.setTitle(service.getTitle());
                    dto.setDescription(service.getDescription());
                    dto.setDuration(service.getDuration());
                    dto.setPrice(service.getPrice());
                    dto.setCategoryId(service.getCategory() != null ? service.getCategory().getId() : null);
                    dto.setCategoryTitle(service.getCategory() != null ? service.getCategory().getTitle() : null);
                    dto.setProviderId(provider.getId());
                    dto.setProviderName(provider.getFirstname() + " " + provider.getLastname());
                    return dto;
                })
                .collect(Collectors.toList())
            : Collections.emptyList();

        // Map schedules
        List<ScheduleResponseDTO> schedules = scheduleRepository.findByProviderId(provider.getId())
            .stream()
            .map(schedule -> {
                ScheduleResponseDTO dto = new ScheduleResponseDTO();
                dto.setId(schedule.getId());
                dto.setStartTime(schedule.getStartTime());
                dto.setEndTime(schedule.getEndTime());
                dto.setProviderId(provider.getId());
                return dto;
            })
            .collect(Collectors.toList());

        // Map reviews (via mongo)
        List<Review> reviewEntities = reviewRepository.findByProviderId(provider.getId());
        List<ReviewResponseDTO> reviews = reviewEntities.stream()
            .map(review -> ReviewResponseDTO.builder()
                .id(review.getId())
                .customerId(review.getCustomerId())
                .customerName(review.getCustomerName())
                .providerId(review.getProviderId())
                .providerName(review.getProviderName())
                .makeupServiceId(review.getMakeupServiceId())
                .rating(review.getRating())
                .comment(review.getComment())
                .dateComment(review.getDateComment())
                .build())
            .collect(Collectors.toList());

        // Calcul moyenne
        Double averageRating = reviews.isEmpty() ? 0.0 :
                reviews.stream().mapToInt(ReviewResponseDTO::getRating).average().orElse(0.0);

        return ProviderDetailResponseDTO.builder()
                .id(provider.getId())
                .firstname(provider.getFirstname())
                .lastname(provider.getLastname())
                .address(provider.getAddress())
                .email(provider.getEmail())                
                .phoneNumber(provider.getPhoneNumber())    
                .averageRating(averageRating)
                .categoriesString(categoriesString)
                .services(services)
                .schedules(schedules)
                .reviews(reviews)
                .build();
    }

    /**
     * Calcul de la moyenne des notes d’un prestataire.
     */
    @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
    public Double getAverageRating(Long providerId) {
        List<Review> reviews = reviewRepository.findByProviderId(providerId);
        if (reviews.isEmpty()) return 0.0;
        double sum = reviews.stream().mapToInt(Review::getRating).sum();
        return Math.round((sum / reviews.size()) * 10) / 10.0;
    }

    /**
     * Recherche de prestataires par nom + ville.
     */
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public List<Provider> searchProvidersByCriteria(String keyword, String location) {
        return providerRepository.findByFirstnameContainingIgnoreCaseAndAddressContainingIgnoreCase(keyword, location);
    }

    /**
     * Affichage du profil du prestataire.
     */
    @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
    public Provider viewProviderProfile(Long providerId) {
        String currentRole = SecurityUtils.getCurrentUserRole();
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentRole == null || currentUserId == null) {
            throw new SecurityException("Utilisateur non authentifié ou rôle introuvable.");
        }
        if ("ROLE_PROVIDER".equals(currentRole) && !providerId.equals(currentUserId)) {
            throw new SecurityException("Accès interdit : vous ne pouvez consulter que votre propre profil.");
        }
        return providerRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Prestataire non trouvé avec l'id : " + providerId));
    }

    /**
     * Retourne la liste de tous les prestataires.
     */
    @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
    public List<Provider> getAllProviders() {
        return providerRepository.findAll();
    }
}
