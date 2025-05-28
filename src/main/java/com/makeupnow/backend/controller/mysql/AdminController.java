package com.makeupnow.backend.controller.mysql;

import com.makeupnow.backend.model.mysql.Customer;
import com.makeupnow.backend.model.mysql.Provider;
import com.makeupnow.backend.model.mysql.User;
import com.makeupnow.backend.service.mysql.AdminService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
 @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

     @PreAuthorize("hasRole('ADMIN')")
     @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        User user = adminService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

 @PreAuthorize("hasRole('ADMIN')")
      @PutMapping("/users/{adminId}/deactivate/{userId}")
    public ResponseEntity<String> deactivateUser(@PathVariable Long adminId, @PathVariable Long userId) {
        if (adminService.deactivateUser(adminId, userId)) {
            return ResponseEntity.ok("Utilisateur désactivé.");
        }
        return ResponseEntity.notFound().build();
    }
 @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{adminId}/reactivate/{userId}")
    public ResponseEntity<String> reactivateUser(@PathVariable Long adminId, @PathVariable Long userId) {
        if (adminService.reactivateUser(adminId, userId)) {
            return ResponseEntity.ok("Utilisateur réactivé.");
        }
        return ResponseEntity.notFound().build();
    }

 @PreAuthorize("hasRole('ADMIN')")   
  @DeleteMapping("/users/{adminId}/delete/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long adminId, @PathVariable Long userId) {
        if (adminService.deleteUser(adminId, userId)) {
            return ResponseEntity.ok("Utilisateur supprimé.");
        }
        return ResponseEntity.notFound().build();
    }
 @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getCustomersByStatus(@RequestParam boolean status) {
        return ResponseEntity.ok(adminService.getCustomersByStatus(status));
    }
 @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/providers")
    public ResponseEntity<List<Provider>> getProvidersByStatus(@RequestParam boolean status) {
        return ResponseEntity.ok(adminService.getProvidersByStatus(status));
    }
}
