package com.makeupnow.backend.controller.mysql;

import com.makeupnow.backend.dto.provider.ProviderResponseDTO;
import com.makeupnow.backend.model.mysql.Provider;
import com.makeupnow.backend.service.mysql.ProviderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/providers")
public class ProviderController {

    @Autowired
    private ProviderService providerService;

    /**
     * 🔍 Recherche de prestataires par mot-clé et ville.
     * Accessible aux clients et à l’admin.
     */
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<List<ProviderResponseDTO>> searchProviders(
            @RequestParam String keyword,
            @RequestParam String location) {
        List<Provider> providers = providerService.searchProvidersByCriteria(keyword, location);
        List<ProviderResponseDTO> dtos = providers.stream()
                .map(providerService::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * 👤 Accès au profil d’un prestataire par l’admin, un client ou le prestataire lui-même.
     */
    @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
    @GetMapping("/{id}/profile")
    public ResponseEntity<ProviderResponseDTO> getProviderProfile(@PathVariable Long id) {
        Provider provider = providerService.viewProviderProfile(id);
        return ResponseEntity.ok(providerService.mapToDTO(provider));
    }

    /**
     * ⭐ Note moyenne d’un prestataire.
     * Visible par le prestataire lui-même, les clients ou l’admin.
     */
    @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
    @GetMapping("/{id}/rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long id) {
        Double rating = providerService.getAverageRating(id);
        return ResponseEntity.ok(rating);
    }

    /**
 * 🔄 Liste de tous les prestataires (providers)
 * Accessible par les clients, les prestataires et l’admin.
 */
@PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
@GetMapping
public ResponseEntity<List<ProviderResponseDTO>> getAllProviders() {
    List<Provider> providers = providerService.getAllProviders(); // il te faut cette méthode dans le service !
    List<ProviderResponseDTO> dtos = providers.stream()
            .map(providerService::mapToDTO)
            .collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
}

}
