package com.makeupnow.backend.controller.mysql;

import com.makeupnow.backend.model.mysql.Provider;
import com.makeupnow.backend.service.mysql.ProviderService;
import com.makeupnow.backend.model.mysql.enums.CertificationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/providers")
public class ProviderController {

    private final ProviderService providerService;

    @Autowired
    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }

    // ✅ Obtenir tous les providers actifs
    @GetMapping
    public ResponseEntity<List<Provider>> getAllProviders() {
        List<Provider> providers = providerService.getAllProviders();
        return ResponseEntity.ok(providers);
    }

    // ✅ Obtenir un provider par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Provider> getProviderById(@PathVariable Long id) {
        Provider provider = providerService.getProviderById(id);
        return ResponseEntity.ok(provider);
    }

    // ✅ Créer un nouveau provider
    @PostMapping
    public ResponseEntity<Provider> createProvider(@RequestBody Provider provider) {
        Provider createdProvider = providerService.createProvider(provider);
        return ResponseEntity.ok(createdProvider);
    }

    // ✅ Mettre à jour un provider
    @PutMapping("/{id}")
    public ResponseEntity<Provider> updateProvider(
            @PathVariable Long id, 
            @RequestBody Provider provider) {
        Provider updatedProvider = providerService.updateProvider(id, provider);
        return ResponseEntity.ok(updatedProvider);
    }

    // ✅ Désactiver un provider
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateProvider(@PathVariable Long id) {
        providerService.deactivateProvider(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Réactiver un provider
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateProvider(@PathVariable Long id) {
        providerService.activateProvider(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Mettre à jour le statut de certification
    @PatchMapping("/{id}/certification")
    public ResponseEntity<Void> updateCertificationStatus(
            @PathVariable Long id, 
            @RequestParam CertificationStatus status) {
        providerService.updateCertificationStatus(id, status);
        return ResponseEntity.noContent().build();
    }
}
