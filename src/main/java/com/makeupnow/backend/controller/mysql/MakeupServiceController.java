package com.makeupnow.backend.controller.mysql;

import com.makeupnow.backend.dto.makeupservice.MakeupServiceCreateDTO;
import com.makeupnow.backend.dto.makeupservice.MakeupServiceResponseDTO;
import com.makeupnow.backend.dto.makeupservice.MakeupServiceUpdateDTO;
import com.makeupnow.backend.model.mysql.MakeupService;
import com.makeupnow.backend.service.mysql.MakeupServiceService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/makeup-services")
public class MakeupServiceController {

    @Autowired
    private MakeupServiceService makeupServiceService;

    @PreAuthorize("hasRole('PROVIDER')")
    @PostMapping
    public ResponseEntity<MakeupServiceResponseDTO> createMakeupService(@RequestBody @Valid MakeupServiceCreateDTO dto) {
        MakeupService created = makeupServiceService.createMakeupServiceFromDTO(dto);
        return ResponseEntity.ok(makeupServiceService.mapToDTO(created));
    }

    @PreAuthorize("hasRole('PROVIDER')")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateMakeupService(
            @PathVariable Long id,
            @RequestBody @Valid MakeupServiceUpdateDTO dto) {
        boolean updated = makeupServiceService.updateMakeupService(id, dto);
        return updated ?
                ResponseEntity.ok("Service mis à jour avec succès.") :
                ResponseEntity.status(404).body("Service non trouvé.");
    }

    @PreAuthorize("hasRole('PROVIDER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMakeupService(@PathVariable Long id) {
        boolean deleted = makeupServiceService.deleteMakeupService(id);
        return deleted ?
                ResponseEntity.ok("Service supprimé avec succès.") :
                ResponseEntity.status(404).body("Service non trouvé.");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<MakeupServiceResponseDTO>> getServicesByCategory(@PathVariable Long categoryId) {
        List<MakeupServiceResponseDTO> services = makeupServiceService.getServicesByCategory(categoryId);
        return ResponseEntity.ok(services);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<MakeupServiceResponseDTO>> getServicesByProvider(@PathVariable Long providerId) {
        List<MakeupServiceResponseDTO> response = makeupServiceService.getServicesByProvider(providerId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<MakeupServiceResponseDTO>> getAllServices() {
        List<MakeupServiceResponseDTO> services = makeupServiceService.getAllServices();
        return ResponseEntity.ok(services);
    }

    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<List<MakeupServiceResponseDTO>> searchServices(
            @RequestParam String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String providerName,
            @RequestParam(required = false) String location) {
        List<MakeupServiceResponseDTO> services = makeupServiceService.searchServicesByCriteria(keyword, category, providerName, location);
        return ResponseEntity.ok(services);
    }
}
