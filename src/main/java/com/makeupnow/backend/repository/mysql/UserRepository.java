package com.makeupnow.backend.repository.mysql;

import com.makeupnow.backend.model.mysql.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndIsActiveTrue(String email);
    Optional<User> findByIdAndIsActiveTrue(Long id);
    boolean existsByEmail(String email);
}
