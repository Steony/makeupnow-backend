package com.makeupnow.backend.controller.mysql;

import com.makeupnow.backend.model.mysql.Category;
import com.makeupnow.backend.service.mysql.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{title}")
    public ResponseEntity<Category> getCategoryByTitle(@PathVariable String title) {
        return ResponseEntity.ok(categoryService.getCategoryByTitle(title));
    }
}
