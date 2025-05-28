package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.model.mysql.User;
import com.makeupnow.backend.model.mysql.enums.Role;
import com.makeupnow.backend.repository.mysql.UserRepository;
import com.makeupnow.backend.factory.UserFactoryDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserFactoryDispatcher userFactoryDispatcher;

    @Autowired
    private UserActionLogService userActionLogService;

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public boolean registerUser(Role role, String firstname, String lastname, String email, String password, String address, String phoneNumber) {
        if (existsByEmail(email)) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà.");
        }

        User user = userFactoryDispatcher.createUser(role, firstname, lastname, email, password);

        user.setAddress(address);
        user.setPhoneNumber(phoneNumber);
        user.setActive(true);

        userRepository.save(user);

        // Log après sauvegarde
        userActionLogService.logActionByUserId(user.getId(), "Création de compte", "Compte créé pour " + email);

        return true;
    }

    public boolean loginUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmailAndIsActiveTrue(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            boolean matches = passwordEncoder.matches(password, user.getPassword());
            if (matches) {
                userActionLogService.logActionByUserId(user.getId(), "Connexion", "Connexion réussie");
                return true;
            } else {
                userActionLogService.logActionByUserId(user.getId(), "Connexion", "Échec de connexion : mauvais mot de passe");
                return false;
            }
        }
        // Optionnel : log tentative de connexion sur email inconnu
        return false;
    }

    public void logout() {
        // Rien ici côté backend pour l'instant
    }
@PreAuthorize("isAuthenticated()")
    @Transactional
    public boolean updateUser(Long id, String firstname, String lastname, String email, String password, String address, String phoneNumber) {
        Optional<User> userOpt = userRepository.findByIdAndIsActiveTrue(id);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Utilisateur non trouvé");
        }
        User user = userOpt.get();

        StringBuilder changes = new StringBuilder();

        if (!user.getFirstname().equals(firstname)) {
            changes.append("Prénom changé de '").append(user.getFirstname()).append("' à '").append(firstname).append("'. ");
            user.setFirstname(firstname);
        }
        if (!user.getLastname().equals(lastname)) {
            changes.append("Nom changé de '").append(user.getLastname()).append("' à '").append(lastname).append("'. ");
            user.setLastname(lastname);
        }
        if (!user.getEmail().equals(email)) {
            changes.append("Email changé de '").append(user.getEmail()).append("' à '").append(email).append("'. ");
            user.setEmail(email);
        }
        // Pour le password, on ne log pas la valeur exacte
        if (!passwordEncoder.matches(password, user.getPassword())) {
            changes.append("Mot de passe modifié. ");
            user.setPassword(passwordEncoder.encode(password));
        }
        if (!user.getAddress().equals(address)) {
            changes.append("Adresse changée. ");
            user.setAddress(address);
        }
        if (!user.getPhoneNumber().equals(phoneNumber)) {
            changes.append("Numéro de téléphone changé de '").append(user.getPhoneNumber()).append("' à '").append(phoneNumber).append("'. ");
            user.setPhoneNumber(phoneNumber);
        }

        userRepository.save(user);

        if (changes.length() > 0) {
            userActionLogService.logActionByUserId(id, "Mise à jour du compte", "Modifications : " + changes.toString());
        } else {
            userActionLogService.logActionByUserId(id, "Mise à jour du compte", "Aucune modification détectée.");
        }

        return true;
    }
}
