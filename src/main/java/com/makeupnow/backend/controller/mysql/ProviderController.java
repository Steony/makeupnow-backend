package com.makeupnow.backend.controller.mysql;

import com.makeupnow.backend.dto.provider.ProviderResponseDTO;
import com.makeupnow.backend.dto.provider.ProviderDetailResponseDTO;
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
     * 👤 Accès au profil DÉTAILLÉ d’un prestataire par l’admin, un client ou le prestataire lui-même.
     * ➜ RENVOIE TOUTES LES INFOS pour la fiche profil (services, créneaux, avis...)
     */
    @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
   @GetMapping("/{id}/profile")
public ResponseEntity<ProviderDetailResponseDTO> getProviderProfile(@PathVariable Long id) {
    Provider provider = providerService.viewProviderProfile(id);
    System.out.println("🟣 Provider dans controller : " + provider);
    ProviderDetailResponseDTO dto = providerService.mapToDetailDTO(provider);
    System.out.println("🟢 DTO généré : " + dto);
    return ResponseEntity.ok(dto);
}


    /**
     * ⭐ Note moyenne d’un prestataire.
     */
    @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
    @GetMapping("/{id}/rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long id) {
        Double rating = providerService.getAverageRating(id);
        return ResponseEntity.ok(rating);
    }

    /**
     * 🔄 Liste de tous les prestataires (providers) - DTO light.
     */
    @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
    @GetMapping
    public ResponseEntity<List<ProviderResponseDTO>> getAllProviders() {
        List<Provider> providers = providerService.getAllProviders();
        List<ProviderResponseDTO> dtos = providers.stream()
                .map(providerService::mapToDTO)
                .collect(Collectors.toList());

        System.out.println("✅ Contrôleur - Providers envoyés (une seule fois normalement) : " + dtos);
        return ResponseEntity.ok(dtos); 
    }
}
