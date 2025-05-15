package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.model.mysql.User;
import com.makeupnow.backend.repository.mysql.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ✅ Méthode pour créer ou mettre à jour un utilisateur
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // ✅ Méthode pour récupérer un utilisateur par son ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // ✅ Méthode pour récupérer tous les utilisateurs
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ✅ Méthode pour supprimer un utilisateur par son ID
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    // ✅ Méthode pour désactiver un utilisateur
    public void deactivateUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setIsActive(false);
            userRepository.save(user);
        }
    }
}
