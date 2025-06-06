package com.makeupnow.backend.controller.mysql;

import com.makeupnow.backend.dto.category.CategoryResponseDTO;
import com.makeupnow.backend.service.mysql.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
    @GetMapping("/{title}")
    public ResponseEntity<CategoryResponseDTO> getCategoryByTitle(@PathVariable String title) {
        return ResponseEntity.ok(categoryService.getCategoryByTitle(title));
    }
}
