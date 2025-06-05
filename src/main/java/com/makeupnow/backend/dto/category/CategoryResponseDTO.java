package com.makeupnow.backend.dto.category;

import lombok.Data;

@Data
public class CategoryResponseDTO {
    private Long id;
    private String title;

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
