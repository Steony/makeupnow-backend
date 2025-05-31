package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.dto.makeupservice.MakeupServiceCreateDTO;
import com.makeupnow.backend.model.mysql.*;
import com.makeupnow.backend.model.mysql.enums.Role;
import com.makeupnow.backend.repository.mysql.*;
import com.makeupnow.backend.exception.ResourceNotFoundException;
import com.makeupnow.backend.security.SecurityUtilsTestHelper;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MakeupServiceServiceTest {

    @Mock
    private MakeupServiceRepository makeupServiceRepository;
    @Mock
    private ProviderRepository providerRepository;
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private MakeupServiceService makeupServiceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Simuler un prestataire connecté
        SecurityUtilsTestHelper.setAuthentication(1L, "provider@email.com", Role.PROVIDER);
    }

    @AfterEach
    void tearDown() {
        SecurityUtilsTestHelper.clearAuthentication();
    }

    @Test
    void testCreateMakeupService_Success() {
        // DTO
        MakeupServiceCreateDTO dto = new MakeupServiceCreateDTO();
        dto.setProviderId(1L);
        dto.setCategoryId(2L);
        dto.setTitle("Service 1");
        dto.setDescription("Desc");
        dto.setDuration(30);
        dto.setPrice(50.0);

        // Mocks
        Provider provider = new Provider(); provider.setId(1L);
        Category category = new Category(); category.setId(2L);
        when(providerRepository.findById(1L)).thenReturn(Optional.of(provider));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));

        MakeupService savedService = MakeupService.builder()
                .id(10L)
                .provider(provider)
                .category(category)
                .title("Service 1")
                .description("Desc")
                .duration(30)
                .price(50.0)
                .build();

        when(makeupServiceRepository.save(any())).thenReturn(savedService);

        // Appel
        MakeupService created = makeupServiceService.createMakeupServiceFromDTO(dto);

        // Vérif
        assertNotNull(created);
        assertEquals("Service 1", created.getTitle());
        assertEquals(1L, created.getProvider().getId());
        verify(makeupServiceRepository).save(any());
    }

    @Test
    void testCreateMakeupService_ProviderNotFound() {
        MakeupServiceCreateDTO dto = new MakeupServiceCreateDTO();
        dto.setProviderId(1L);
        dto.setCategoryId(2L);

        when(providerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            makeupServiceService.createMakeupServiceFromDTO(dto);
        });
    }

    @Test
    void testCreateMakeupService_CategoryNotFound() {
        MakeupServiceCreateDTO dto = new MakeupServiceCreateDTO();
        dto.setProviderId(1L);
        dto.setCategoryId(2L);

        when(providerRepository.findById(1L)).thenReturn(Optional.of(new Provider()));
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            makeupServiceService.createMakeupServiceFromDTO(dto);
        });
    }
}
