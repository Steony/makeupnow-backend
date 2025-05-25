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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth") // 🔒 Swagger demandera un token JWT
@RestController
@RequestMapping("/api/makeup-services")
public class MakeupServiceController {

    @Autowired
    private MakeupServiceService makeupServiceService;

  @PostMapping
public ResponseEntity<MakeupService> createMakeupService(@RequestBody @Valid MakeupServiceCreateDTO dto) {
    MakeupService created = makeupServiceService.createMakeupServiceFromDTO(dto);
    return ResponseEntity.ok(created);
}

 @PutMapping("/{id}")
public ResponseEntity<String> updateMakeupService(
        @PathVariable Long id,
        @RequestBody MakeupServiceUpdateDTO dto) {

    boolean updated = makeupServiceService.updateMakeupService(id, dto);
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
public ResponseEntity<List<MakeupServiceResponseDTO>> getServicesByCategory(@PathVariable Long categoryId) {
    List<MakeupServiceResponseDTO> services = makeupServiceService.getServicesByCategory(categoryId);
    return ResponseEntity.ok(services);
}


    @GetMapping("/provider/{providerId}")
public ResponseEntity<List<MakeupServiceResponseDTO>> getServicesByProvider(@PathVariable Long providerId) {
    List<MakeupServiceResponseDTO> response = makeupServiceService.getServicesByProvider(providerId);
    return ResponseEntity.ok(response);
}


   @GetMapping
public ResponseEntity<List<MakeupServiceResponseDTO>> getAllServices() {
    List<MakeupServiceResponseDTO> services = makeupServiceService.getAllServices();
    return ResponseEntity.ok(services);
}

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
