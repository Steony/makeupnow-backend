package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.dto.provider.ProviderResponseDTO;
import com.makeupnow.backend.model.mongo.Review;
import com.makeupnow.backend.model.mysql.Provider;
import com.makeupnow.backend.repository.mongo.ReviewRepository;
import com.makeupnow.backend.repository.mysql.ProviderRepository;
import com.makeupnow.backend.security.SecurityUtils;
import com.makeupnow.backend.exception.ResourceNotFoundException;

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
     * Accessible par les clients et l’admin uniquement.
     */
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public List<Provider> searchProvidersByCriteria(String keyword, String location) {
        return providerRepository.findByFirstnameContainingIgnoreCaseAndAddressContainingIgnoreCase(keyword, location);
    }

    /**
     * Affichage du profil du prestataire par l’admin, le client ou le prestataire concerné.
     */
  @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
public Provider viewProviderProfile(Long providerId) {
    String currentRole = SecurityUtils.getCurrentUserRole();
    Long currentUserId = SecurityUtils.getCurrentUserId();

    // 🚨 Protection contre les erreurs nulles
    if (currentRole == null || currentUserId == null) {
        throw new SecurityException("Utilisateur non authentifié ou rôle introuvable.");
    }

    // 🧑‍🔧 Cas particulier : un PROVIDER ne peut voir que SON profil
    if ("ROLE_PROVIDER".equals(currentRole) && !providerId.equals(currentUserId)) {
        throw new SecurityException("Accès interdit : vous ne pouvez consulter que votre propre profil.");
    }

    return providerRepository.findById(providerId)
            .orElseThrow(() -> new ResourceNotFoundException("Prestataire non trouvé avec l'id : " + providerId));
}



    /**
     * Mapping d’un Provider vers un DTO de réponse.
     */
    public ProviderResponseDTO mapToDTO(Provider provider) {
        return ProviderResponseDTO.builder()
                .id(provider.getId())
                .firstname(provider.getFirstname())
                .lastname(provider.getLastname())
                .address(provider.getAddress())
                .averageRating(getAverageRating(provider.getId()))
                .build();
    }

/**
 * Retourne la liste de tous les prestataires.
 */
@PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
public List<Provider> getAllProviders() {
    return providerRepository.findAll();
}


}
