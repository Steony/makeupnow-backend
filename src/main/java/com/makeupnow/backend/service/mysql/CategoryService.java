package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.dto.category.CategoryResponseDTO;
import com.makeupnow.backend.exception.ResourceNotFoundException;
import com.makeupnow.backend.model.mysql.Category;
import com.makeupnow.backend.repository.mysql.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll(Sort.by("title"))
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
    public CategoryResponseDTO getCategoryByTitle(String title) {
        Category category = categoryRepository.findByTitle(title)
                .orElseThrow(() -> new ResourceNotFoundException("Category non trouvée avec le titre : " + title));
        return mapToDTO(category);
    }

    private CategoryResponseDTO mapToDTO(Category category) {
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setTitle(category.getTitle());
        return dto;
    }
}
