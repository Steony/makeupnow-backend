package com.makeupnow.backend.repository.mysql;

import com.makeupnow.backend.model.mysql.MakeupService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MakeupServiceRepository extends JpaRepository<MakeupService, Long> {
    
    List<MakeupService> findByCategoryId(Long categoryId);

    List<MakeupService> findByProviderId(Long providerId);

    List<MakeupService> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        String keyword1, String keyword2);
}
