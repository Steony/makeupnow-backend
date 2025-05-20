package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.model.mysql.Category;
import com.makeupnow.backend.repository.mysql.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @PreAuthorize("isAuthenticated()")  // accès à tous les utilisateurs connectés
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @PreAuthorize("isAuthenticated()")
    public Category getCategoryByTitle(String title) {
        return categoryRepository.findByTitle(title)
                .orElseThrow(() -> new RuntimeException("Category non trouvée avec le titre : " + title));
    }
}

