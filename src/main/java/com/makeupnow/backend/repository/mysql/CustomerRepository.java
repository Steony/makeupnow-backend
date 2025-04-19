package com.makeupnow.backend.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.makeupnow.backend.model.mysql.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // Tu pourras ajouter des méthodes personnalisées ici plus tard
}
