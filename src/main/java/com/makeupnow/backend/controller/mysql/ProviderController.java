package com.makeupnow.backend.controller.mysql;

import com.makeupnow.backend.model.mysql.Provider;
import com.makeupnow.backend.service.mysql.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class ProviderController {

    @Autowired
    private ProviderService providerService;

    // Accessible uniquement par les Customers
    @GetMapping("/search")
    public ResponseEntity<List<Provider>> searchProviders(
        @RequestParam String keyword,
        @RequestParam String location) {
        List<Provider> providers = providerService.searchProvidersByCriteria(keyword, location);
        return ResponseEntity.ok(providers);
    }

    // Accessible uniquement par les Customers
    @GetMapping("/{id}/profile")
    public ResponseEntity<Provider> getProviderProfile(@PathVariable Long id) {
        Provider provider = providerService.viewProviderProfile(id);
        return ResponseEntity.ok(provider);
    }

    // Accessible à tous (par ex : customer, admin, provider), ou restreindre si besoin
    @GetMapping("/{id}/rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long id) {
        Double rating = providerService.getAverageRating(id);
        return ResponseEntity.ok(rating);
    }
}
