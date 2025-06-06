package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.dto.makeupservice.MakeupServiceCreateDTO;
import com.makeupnow.backend.dto.makeupservice.MakeupServiceResponseDTO;
import com.makeupnow.backend.dto.makeupservice.MakeupServiceUpdateDTO;
import com.makeupnow.backend.exception.ResourceNotFoundException;
import com.makeupnow.backend.model.mysql.Category;
import com.makeupnow.backend.model.mysql.MakeupService;
import com.makeupnow.backend.model.mysql.Provider;
import com.makeupnow.backend.repository.mysql.CategoryRepository;
import com.makeupnow.backend.repository.mysql.MakeupServiceRepository;
import com.makeupnow.backend.repository.mysql.ProviderRepository;
import com.makeupnow.backend.security.SecurityUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MakeupServiceService {

    @Autowired
    private MakeupServiceRepository makeupServiceRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @PreAuthorize("hasRole('PROVIDER')")
    public MakeupService createMakeupServiceFromDTO(MakeupServiceCreateDTO dto) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!dto.getProviderId().equals(currentUserId)) {
            throw new AccessDeniedException("Vous ne pouvez créer un service que pour votre propre compte.");
        }

        Provider provider = providerRepository.findById(dto.getProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("Prestataire non trouvé"));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée"));

        MakeupService service = MakeupService.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .duration(dto.getDuration())
                .price(dto.getPrice())
                .provider(provider)
                .category(category)
                .build();

        return makeupServiceRepository.save(service);
    }

    @PreAuthorize("hasRole('PROVIDER')")
    public boolean updateMakeupService(Long id, MakeupServiceUpdateDTO dto) {
        MakeupService existing = makeupServiceRepository.findById(id).orElse(null);
        if (existing == null) return false;

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!existing.getProvider().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Vous ne pouvez modifier que vos propres services.");
        }

        Provider provider = providerRepository.findById(dto.getProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("Prestataire non trouvé"));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée"));

        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setDuration(dto.getDuration());
        existing.setPrice(dto.getPrice());
        existing.setProvider(provider);
        existing.setCategory(category);

        makeupServiceRepository.save(existing);
        return true;
    }

    @PreAuthorize("hasRole('PROVIDER')")
    public boolean deleteMakeupService(Long serviceId) {
        MakeupService service = makeupServiceRepository.findById(serviceId).orElse(null);
        if (service == null) return false;

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!service.getProvider().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Vous ne pouvez supprimer que vos propres services.");
        }

        makeupServiceRepository.delete(service);
        return true;
    }

    @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
    public List<MakeupServiceResponseDTO> getServicesByCategory(Long categoryId) {
        List<MakeupService> services = makeupServiceRepository.findByCategoryId(categoryId);
        return services.stream()
            .map(service -> {
                System.out.println(">>> [DEBUG] Mapping entité -> DTO, service = " + service.getId());
                return mapToDTO(service);
            })
            .toList();
    }

    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN', 'CLIENT')")
    public List<MakeupServiceResponseDTO> getServicesByProvider(Long providerId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        String currentUserRole = SecurityUtils.getCurrentUserRole();

        System.out.println("🔍 currentUserId: " + currentUserId + ", providerId (reçu): " + providerId);

        // 🔒 Si l'utilisateur est Provider, il ne peut voir que ses propres services
        if ("ROLE_PROVIDER".equals(currentUserRole) && !currentUserId.equals(providerId)) {
            throw new AccessDeniedException("Vous ne pouvez consulter que vos propres services.");
        }

        List<MakeupService> services = makeupServiceRepository.findByProviderId(providerId);
        return services.stream()
            .map(service -> {
                System.out.println(">>> [DEBUG] Mapping entité -> DTO, service = " + service.getId());
                return mapToDTO(service);
            })
            .toList();
    }

    @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
    public List<MakeupServiceResponseDTO> getAllServices() {
        System.out.println(">>> [DEBUG] Controller: getAllMakeupServices appelé !");
        List<MakeupService> services = makeupServiceRepository.findAll();
        return services.stream()
            .map(service -> {
                System.out.println(">>> [DEBUG] Mapping entité -> DTO, service = " + service.getId());
                return mapToDTO(service);
            })
            .toList();
    }

    @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
    public List<MakeupServiceResponseDTO> searchServicesByCriteria(String keyword, String category, String providerName, String location) {
        List<MakeupService> services = makeupServiceRepository
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
        return services.stream()
            .map(service -> {
                System.out.println(">>> [DEBUG] Mapping entité -> DTO, service = " + service.getId());
                return mapToDTO(service);
            })
            .toList();
    }

    public MakeupServiceResponseDTO mapToDTO(MakeupService service) {
        System.out.println(">>> [DEBUG] mapToDTO appelé pour service = " + service.getId());
        MakeupServiceResponseDTO dto = new MakeupServiceResponseDTO();
        dto.setId(service.getId());
        dto.setTitle(service.getTitle());
        dto.setDescription(service.getDescription());
        dto.setDuration(service.getDuration());
        dto.setPrice(service.getPrice());

        if (service.getCategory() != null) {
            dto.setCategoryId(service.getCategory().getId());
            dto.setCategoryTitle(service.getCategory().getTitle());
        }

        if (service.getProvider() != null) {
            dto.setProviderId(service.getProvider().getId());
            dto.setProviderName(service.getProvider().getFirstname() + " " + service.getProvider().getLastname());
        }

        return dto;
    }
}
