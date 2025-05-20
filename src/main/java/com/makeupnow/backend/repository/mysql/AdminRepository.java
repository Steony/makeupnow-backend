package com.makeupnow.backend.repository.mysql;

import com.makeupnow.backend.model.mysql.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    // Ajouter des méthodes personnalisées si besoin
}
