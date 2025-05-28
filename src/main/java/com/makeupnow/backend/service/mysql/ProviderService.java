package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.dto.provider.ProviderResponseDTO;
import com.makeupnow.backend.model.mongo.Review;
import com.makeupnow.backend.model.mysql.Provider;
import com.makeupnow.backend.repository.mongo.ReviewRepository;
import com.makeupnow.backend.repository.mysql.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProviderService {

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    // Calcul de la moyenne des notes sur 5 pour un provider donné
     @PreAuthorize("isAuthenticated()")
    public Double getAverageRating(Long providerId) {
        List<Review> reviews = reviewRepository.findByProviderId(providerId);

        if (reviews.isEmpty()) {
            return 0.0; // Pas de reviews = note 0
        }

        double sum = reviews.stream()
                            .mapToInt(Review::getRating) // récupérer la note entière
                            .sum();

        double average = sum / reviews.size();

        // Arrondi à une décimale (ex: 4.53 -> 4.5)
        return Math.round(average * 10) / 10.0;
    }

    // Recherche par critères pour Customer et Admin
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public List<Provider> searchProvidersByCriteria(String keyword, String location) {
        return providerRepository.findByFirstnameContainingIgnoreCaseAndAddressContainingIgnoreCase(keyword, location);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER') or (hasRole('PROVIDER') and #providerId == authentication.principal.id)")
public Provider viewProviderProfile(Long providerId) {
    return providerRepository.findById(providerId)
        .orElseThrow(() -> new RuntimeException("Provider non trouvé"));
}


    public ProviderResponseDTO mapToDTO(Provider provider) {
    return ProviderResponseDTO.builder()
            .id(provider.getId())
            .firstname(provider.getFirstname())
            .lastname(provider.getLastname())
            .address(provider.getAddress())
            .isCertified(provider.isCertified())
            .averageRating(getAverageRating(provider.getId()))
            .build();
}

}
