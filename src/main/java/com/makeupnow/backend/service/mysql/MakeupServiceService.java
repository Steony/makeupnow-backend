package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.dto.makeupservice.MakeupServiceCreateDTO;
import com.makeupnow.backend.dto.makeupservice.MakeupServiceResponseDTO;
import com.makeupnow.backend.dto.makeupservice.MakeupServiceUpdateDTO;
import com.makeupnow.backend.model.mysql.Category;
import com.makeupnow.backend.model.mysql.MakeupService;
import com.makeupnow.backend.model.mysql.Provider;
import com.makeupnow.backend.repository.mysql.CategoryRepository;
import com.makeupnow.backend.repository.mysql.MakeupServiceRepository;
import com.makeupnow.backend.repository.mysql.ProviderRepository;

import org.springframework.beans.factory.annotation.Autowired;
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
        Provider provider = providerRepository.findById(dto.getProviderId())
                .orElseThrow(() -> new RuntimeException("Prestataire non trouvé"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));

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
    MakeupService existing = makeupServiceRepository.findById(id)
            .orElse(null);

    if (existing == null) return false;

    Provider provider = providerRepository.findById(dto.getProviderId())
            .orElseThrow(() -> new RuntimeException("Prestataire non trouvé"));
    Category category = categoryRepository.findById(dto.getCategoryId())
            .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));

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
        if (!makeupServiceRepository.existsById(serviceId)) {
            return false;
        }
        makeupServiceRepository.deleteById(serviceId);
        return true;
    }

    @PreAuthorize("isAuthenticated()")
public List<MakeupServiceResponseDTO> getServicesByCategory(Long categoryId) {
    List<MakeupService> services = makeupServiceRepository.findByCategoryId(categoryId);
    return services.stream().map(this::mapToDTO).toList();
}

    @PreAuthorize("isAuthenticated()")
    public List<MakeupServiceResponseDTO> getServicesByProvider(Long providerId) {
        List<MakeupService> services = makeupServiceRepository.findByProviderId(providerId);
        return services.stream().map(this::mapToDTO).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<MakeupServiceResponseDTO> getAllServices() {
        List<MakeupService> services = makeupServiceRepository.findAll();
        return services.stream().map(this::mapToDTO).toList();
    }

    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public List<MakeupServiceResponseDTO> searchServicesByCriteria(String keyword, String category, String providerName, String location) {
        List<MakeupService> services = makeupServiceRepository
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
        return services.stream().map(this::mapToDTO).toList();
    }

    // 🔁 Mapping centralisé
    private MakeupServiceResponseDTO mapToDTO(MakeupService service) {
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
