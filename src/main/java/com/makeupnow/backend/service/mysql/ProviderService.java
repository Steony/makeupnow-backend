package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.model.mysql.Provider;
import com.makeupnow.backend.repository.mysql.ProviderRepository;
import com.makeupnow.backend.model.mysql.enums.CertificationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProviderService {
    
    private final ProviderRepository providerRepository;

    @Autowired
    public ProviderService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    // Obtenir tous les providers actifs
    public List<Provider> getAllProviders() {
        return providerRepository.findByIsActiveTrue();
    }

    // Obtenir un provider par son ID
    public Provider getProviderById(Long id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found with ID: " + id));
    }

    // Créer un nouveau provider
    public Provider createProvider(Provider provider) {
        provider.setIsActive(true);
        provider.setCertificationStatus(CertificationStatus.REJECTED);
        return providerRepository.save(provider);
    }

    // Mettre à jour les informations d'un provider
    public Provider updateProvider(Long id, Provider updatedProvider) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found with ID: " + id));
        
        // Mise à jour des informations héritées de User
        provider.setFirstName(updatedProvider.getFirstName());
        provider.setLastName(updatedProvider.getLastName());
        provider.setEmail(updatedProvider.getEmail());
        provider.setPhoneNumber(updatedProvider.getPhoneNumber());

        // Mise à jour des informations spécifiques à Provider
        provider.setCertificationStatus(updatedProvider.getCertificationStatus());
        
        return providerRepository.save(provider);
    }

    // Désactiver un provider
    public void deactivateProvider(Long id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found with ID: " + id));
        
        provider.setIsActive(false);
        providerRepository.save(provider);
    }

    // Réactiver un provider
    public void activateProvider(Long id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found with ID: " + id));
        
        provider.setIsActive(true);
        providerRepository.save(provider);
    }

    // Mettre à jour le statut de certification d'un provider
    public void updateCertificationStatus(Long id, CertificationStatus status) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found with ID: " + id));
        
        provider.setCertificationStatus(status);
        providerRepository.save(provider);
    }
}
