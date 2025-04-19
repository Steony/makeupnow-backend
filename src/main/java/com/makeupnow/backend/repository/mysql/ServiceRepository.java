package com.makeupnow.backend.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.makeupnow.backend.model.mysql.Service;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    // Tu pourras ajouter des méthodes custom ici plus tard
}
