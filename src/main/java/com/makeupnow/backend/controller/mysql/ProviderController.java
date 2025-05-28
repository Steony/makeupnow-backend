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

    // ✅ Recherche de prestataires par mot-clé + ville - accès Customer ou Admin
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
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

    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER') or (hasRole('PROVIDER') and #id == authentication.principal.id)")
@GetMapping("/{id}/profile")
public ResponseEntity<ProviderResponseDTO> getProviderProfile(@PathVariable Long id) {
    Provider provider = providerService.viewProviderProfile(id);
    return ResponseEntity.ok(providerService.mapToDTO(provider));
}

    // ✅ Note moyenne d’un provider - si tu veux la garder séparément
     @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER') or (hasRole('PROVIDER') and #providerId == authentication.principal.id)")
    @GetMapping("/{id}/rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long id) {
        Double rating = providerService.getAverageRating(id);
        return ResponseEntity.ok(rating);
    }
}
