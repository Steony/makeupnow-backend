package com.makeupnow.backend.controller.mysql;

import com.makeupnow.backend.model.mysql.MakeupService;
import com.makeupnow.backend.service.mysql.MakeupServiceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/makeup-services")
public class MakeupServiceController {

    @Autowired
    private MakeupServiceService makeupServiceService;

    @PostMapping
    public ResponseEntity<MakeupService> createMakeupService(@RequestBody MakeupService makeupService) {
        MakeupService created = makeupServiceService.createMakeupService(makeupService);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateMakeupService(@PathVariable Long id, @RequestBody MakeupService makeupService) {
        makeupService.setId(id);
        boolean updated = makeupServiceService.updateMakeupService(makeupService);
        if (updated) {
            return ResponseEntity.ok("Service mis à jour avec succès.");
        } else {
            return ResponseEntity.status(404).body("Service non trouvé.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMakeupService(@PathVariable Long id) {
        boolean deleted = makeupServiceService.deleteMakeupService(id);
        if (deleted) {
            return ResponseEntity.ok("Service supprimé avec succès.");
        } else {
            return ResponseEntity.status(404).body("Service non trouvé.");
        }
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<MakeupService>> getServicesByCategory(@PathVariable Long categoryId) {
        List<MakeupService> services = makeupServiceService.getServicesByCategory(categoryId);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<MakeupService>> getServicesByProvider(@PathVariable Long providerId) {
        List<MakeupService> services = makeupServiceService.getServicesByProvider(providerId);
        return ResponseEntity.ok(services);
    }

    @GetMapping
    public ResponseEntity<List<MakeupService>> getAllServices() {
        List<MakeupService> services = makeupServiceService.getAllServices();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/search")
    public ResponseEntity<List<MakeupService>> searchServices(
        @RequestParam String keyword,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String providerName,
        @RequestParam(required = false) String location) {

        List<MakeupService> services = makeupServiceService.searchServicesByCriteria(keyword, category, providerName, location);
        return ResponseEntity.ok(services);
    }
}
