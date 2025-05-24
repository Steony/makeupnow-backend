package com.makeupnow.backend.controller.mysql;

import com.makeupnow.backend.model.mysql.Customer;
import com.makeupnow.backend.model.mysql.Provider;
import com.makeupnow.backend.model.mysql.User;
import com.makeupnow.backend.service.mysql.AdminService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        User user = adminService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

     @PutMapping("/users/{adminId}/deactivate/{userId}")
    public ResponseEntity<String> deactivateUser(@PathVariable Long adminId, @PathVariable Long userId) {
        if (adminService.deactivateUser(adminId, userId)) {
            return ResponseEntity.ok("Utilisateur désactivé.");
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/users/{adminId}/reactivate/{userId}")
    public ResponseEntity<String> reactivateUser(@PathVariable Long adminId, @PathVariable Long userId) {
        if (adminService.reactivateUser(adminId, userId)) {
            return ResponseEntity.ok("Utilisateur réactivé.");
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/users/{adminId}/delete/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long adminId, @PathVariable Long userId) {
        if (adminService.deleteUser(adminId, userId)) {
            return ResponseEntity.ok("Utilisateur supprimé.");
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getCustomersByStatus(@RequestParam boolean status) {
        return ResponseEntity.ok(adminService.getCustomersByStatus(status));
    }

    @GetMapping("/providers")
    public ResponseEntity<List<Provider>> getProvidersByStatus(@RequestParam boolean status) {
        return ResponseEntity.ok(adminService.getProvidersByStatus(status));
    }
}
