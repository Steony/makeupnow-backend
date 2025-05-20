package com.makeupnow.backend.repository.mysql;

import com.makeupnow.backend.model.mysql.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {


    // Recherche selon un mot clé et localisation (à adapter avec une requête personnalisée si besoin)
    List<Provider> findByFirstnameContainingIgnoreCaseAndAddressContainingIgnoreCase(String keyword, String location);
    
    List<Provider> findByIsActive(boolean isActive);
}
