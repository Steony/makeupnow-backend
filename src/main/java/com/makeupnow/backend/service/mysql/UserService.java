package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.model.mysql.User;
import com.makeupnow.backend.model.mysql.enums.Role;
import com.makeupnow.backend.repository.mysql.UserRepository;
import com.makeupnow.backend.security.LoginAttemptService;
import com.makeupnow.backend.security.SecurityUtils;
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

    @Autowired
    private LoginAttemptService loginAttemptService;

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

      @Transactional
public boolean registerUser(
    Role role,
    String firstname,
    String lastname,
    String email,
    String motDePasse,
    String address,
    String phoneNumber
    // SUPPRIMER Boolean isCertified
) {
    if (existsByEmail(email)) {
        throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà.");
    }

    // Création du user
    User user = userFactoryDispatcher.createUser(role, firstname, lastname, email, motDePasse);
    user.setAddress(address);
    user.setPhoneNumber(phoneNumber);
    user.setActive(true);

    // Si c’est un Provider, on laisse isCertified à false (par défaut dans l'entité !)
    // On ne set plus ce champ à partir d’un input.

    // Sauvegarde en base
    userRepository.save(user);

    // Log métier (plus de mention du status "certifié")
    String logMsg = "Compte créé pour " + email;
    userActionLogService.logActionByUserId(user.getId(), "Création de compte", logMsg);

    return true;
}


    public boolean loginUser(String email, String password) {
        
    if (loginAttemptService.isBlocked(email)) {
    // 🔒 Tentative bloquée → log anonyme ou lié à l'utilisateur si existant
    Optional<User> blockedUser = userRepository.findByEmail(email);
    if (blockedUser.isPresent()) {
        userActionLogService.logActionByUserId(
            blockedUser.get().getId(),
            "Blocage de tentative de connexion",
            "Trop de tentatives échouées. Compte temporairement bloqué (email : " + email + ")"
        );
    } else {
        userActionLogService.logActionByUserId(
            null,
            "Blocage de tentative de connexion",
            "Trop de tentatives échouées avec un email inconnu : " + email
        );
    }

    throw new SecurityException("Trop de tentatives. Compte temporairement bloqué.");
}



    Optional<User> userOpt = userRepository.findByEmailAndIsActiveTrue(email);
    if (userOpt.isPresent()) {
        User user = userOpt.get();
        boolean matches = passwordEncoder.matches(password, user.getPassword());
        if (matches) {
            loginAttemptService.loginSucceeded(email);
            userActionLogService.logActionByUserId(user.getId(), "Connexion", "Connexion réussie");
            return true;
        } else {
            loginAttemptService.loginFailed(email);
            userActionLogService.logActionByUserId(user.getId(), "Connexion", "Échec de connexion : mauvais mot de passe");
            return false;
        }
    }

    loginAttemptService.loginFailed(email); // tentative même si email inconnu
    return false;
}

    public void logout() {
        // Rien ici côté backend pour l'instant
    }
 @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
@Transactional
public boolean updateUser(Long id, String firstname, String lastname, String email, String password, String address, String phoneNumber) {
    Long currentUserId = SecurityUtils.getCurrentUserId();
    String currentUserRole = SecurityUtils.getCurrentUserRole();

    Optional<User> userOpt = userRepository.findByIdAndIsActiveTrue(id);
    if (userOpt.isEmpty()) {
        throw new IllegalArgumentException("Utilisateur non trouvé");
    }
    User user = userOpt.get();

    // 🔐 Vérification des droits :
    // → l'utilisateur ne peut modifier que son propre compte
    // → un ADMIN peut tout modifier sauf les autres ADMIN
    if (!id.equals(currentUserId)) {
        if (!"ADMIN".equals(currentUserRole)) {
            throw new SecurityException("Vous ne pouvez modifier que votre propre compte.");
        } else if (user.getRole() == Role.ADMIN) {
            throw new SecurityException("Un administrateur ne peut pas modifier le compte d’un autre administrateur.");
        }
    }

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

 @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
@Transactional
public boolean updatePassword(String currentPassword, String newPassword) {
    Long currentUserId = SecurityUtils.getCurrentUserId();
    Optional<User> userOpt = userRepository.findByIdAndIsActiveTrue(currentUserId);
    if (userOpt.isEmpty()) {
        throw new IllegalArgumentException("Utilisateur non trouvé.");
    }
    User user = userOpt.get();

    // Vérifier l’ancien mot de passe
    if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
        throw new SecurityException("Mot de passe actuel incorrect.");
    }

    // Mettre à jour le mot de passe
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);

    userActionLogService.logActionByUserId(
        currentUserId,
        "Changement de mot de passe",
        "Mot de passe modifié avec succès."
    );
    return true;
}


public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
}


}
