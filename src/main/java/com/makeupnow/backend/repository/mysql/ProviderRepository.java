package com.makeupnow.backend.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.makeupnow.backend.model.mysql.Provider;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {
}

