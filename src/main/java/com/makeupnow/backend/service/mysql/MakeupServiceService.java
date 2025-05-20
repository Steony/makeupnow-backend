package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.model.mysql.MakeupService;
import com.makeupnow.backend.repository.mysql.MakeupServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MakeupServiceService {

    @Autowired
    private MakeupServiceRepository makeupServiceRepository;

    @PreAuthorize("hasRole('PROVIDER')")
    public MakeupService createMakeupService(MakeupService makeupService) {
        return makeupServiceRepository.save(makeupService);
    }

    @PreAuthorize("hasRole('PROVIDER')")
    public boolean updateMakeupService(MakeupService makeupService) {
        if (!makeupServiceRepository.existsById(makeupService.getId())) {
            return false;
        }
        makeupServiceRepository.save(makeupService);
        return true;
    }

    @PreAuthorize("hasRole('PROVIDER')")
    public boolean deleteMakeupService(Long serviceId) {
        if (!makeupServiceRepository.existsById(serviceId)) {
            return false;
        }
        makeupServiceRepository.deleteById(serviceId);
        return true;
    }

    @PreAuthorize("isAuthenticated()")
    public List<MakeupService> getServicesByCategory(Long categoryId) {
        return makeupServiceRepository.findByCategoryId(categoryId);
    }

    @PreAuthorize("isAuthenticated()")
    public List<MakeupService> getServicesByProvider(Long providerId) {
        return makeupServiceRepository.findByProviderId(providerId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<MakeupService> getAllServices() {
        return makeupServiceRepository.findAll();
    }

    @PreAuthorize("hasRole('CLIENT')")
    public List<MakeupService> searchServicesByCriteria(String keyword, String category, String providerName, String location) {
       
        return makeupServiceRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
    }
}
