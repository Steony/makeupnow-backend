package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.model.mongo.Review;
import com.makeupnow.backend.model.mysql.Provider;
import com.makeupnow.backend.repository.mongo.ReviewRepository;
import com.makeupnow.backend.repository.mysql.ProviderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

public class ProviderServiceTest {

    @Mock private ProviderRepository providerRepository;
    @Mock private ReviewRepository reviewRepository;

    @InjectMocks private ProviderService providerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAverageRating_WhenNoReviews() {
        when(reviewRepository.findByProviderId(1L)).thenReturn(List.of());

        Double average = providerService.getAverageRating(1L);

        assertEquals(0.0, average);
    }

    @Test
    void testGetAverageRating_WithReviews() {
        Review r1 = new Review(); r1.setRating(4);
        Review r2 = new Review(); r2.setRating(5);
        when(reviewRepository.findByProviderId(1L)).thenReturn(List.of(r1, r2));

        Double average = providerService.getAverageRating(1L);

        assertEquals(4.5, average);
    }

    @Test
    void testSearchProvidersByCriteria() {
        Provider p = new Provider(); p.setId(1L); p.setFirstname("Alice");
        when(providerRepository.findByFirstnameContainingIgnoreCaseAndAddressContainingIgnoreCase("Alice", "Paris"))
                .thenReturn(List.of(p));

        List<Provider> providers = providerService.searchProvidersByCriteria("Alice", "Paris");

        assertEquals(1, providers.size());
        assertEquals("Alice", providers.get(0).getFirstname());
    }

    @Test
    void testViewProviderProfile_AsAdminOrCustomer() {
        Provider p = new Provider(); p.setId(1L);
        when(providerRepository.findById(1L)).thenReturn(Optional.of(p));

        // On simule un admin (pas de vérif de profil)
        var profile = providerService.viewProviderProfile(1L);

        assertEquals(1L, profile.getId());
    }

    @Test
    void testMapToDTO() {
        Provider p = new Provider();
        p.setId(1L);
        p.setFirstname("John");
        p.setLastname("Doe");
        p.setAddress("Paris");
        p.setCertified(true);

        // On stub la moyenne à 4.5
        when(reviewRepository.findByProviderId(1L)).thenReturn(List.of(new Review() {{ setRating(4); }}, new Review() {{ setRating(5); }}));

        var dto = providerService.mapToDTO(p);

        assertEquals("John", dto.getFirstname());
        assertEquals("Doe", dto.getLastname());
        assertEquals(4.5, dto.getAverageRating());
    }
}
