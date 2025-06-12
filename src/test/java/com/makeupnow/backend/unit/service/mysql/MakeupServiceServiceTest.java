package com.makeupnow.backend.unit.service.mysql;

import com.makeupnow.backend.dto.makeupservice.MakeupServiceCreateDTO;
import com.makeupnow.backend.dto.makeupservice.MakeupServiceUpdateDTO;
import com.makeupnow.backend.model.mysql.*;
import com.makeupnow.backend.model.mysql.enums.Role;
import com.makeupnow.backend.repository.mysql.*;
import com.makeupnow.backend.exception.ResourceNotFoundException;
import com.makeupnow.backend.service.mysql.MakeupServiceService;
import com.makeupnow.backend.unit.security.SecurityUtilsTestHelper;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
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

    @Test
void testCreateMakeupService_AccessDeniedException() {
    // Simule un provider connecté, mais tente de créer pour un autre provider
    SecurityUtilsTestHelper.setAuthentication(123L, "hacker@email.com", Role.PROVIDER);

    MakeupServiceCreateDTO dto = new MakeupServiceCreateDTO();
    dto.setProviderId(1L); // pas 123L !
    dto.setCategoryId(2L);

    Exception ex = assertThrows(org.springframework.security.access.AccessDeniedException.class, () ->
        makeupServiceService.createMakeupServiceFromDTO(dto)
    );
    assertEquals("Vous ne pouvez créer un service que pour votre propre compte.", ex.getMessage());
}

// Test : updateMakeupService - succès (provider correct)
@Test
void testUpdateMakeupService_Success() {
    Long serviceId = 10L;
    Long providerId = 1L;
    Long categoryId = 2L;

    // Service existant
    Provider provider = new Provider(); provider.setId(providerId);
    Category category = new Category(); category.setId(categoryId);
    MakeupService service = MakeupService.builder()
            .id(serviceId)
            .provider(provider)
            .category(category)
            .title("Ancien")
            .description("Ancienne desc")
            .duration(20)
            .price(30.0)
            .build();

    // DTO mise à jour
    MakeupServiceUpdateDTO dto = new MakeupServiceUpdateDTO();
    dto.setProviderId(providerId);
    dto.setCategoryId(categoryId);
    dto.setTitle("Nouveau titre");
    dto.setDescription("Nouvelle desc");
    dto.setDuration(45);
    dto.setPrice(60.0);

    when(makeupServiceRepository.findById(serviceId)).thenReturn(Optional.of(service));
    when(providerRepository.findById(providerId)).thenReturn(Optional.of(provider));
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
    when(makeupServiceRepository.save(any())).thenReturn(service);

    boolean result = makeupServiceService.updateMakeupService(serviceId, dto);

    assertTrue(result);
    assertEquals("Nouveau titre", service.getTitle());
    assertEquals(45, service.getDuration());
    assertEquals(60.0, service.getPrice());
    verify(makeupServiceRepository).save(service);
}

// Test : updateMakeupService - provider différent (access denied)
@Test
void testUpdateMakeupService_AccessDenied() {
    Long serviceId = 10L;
    // Service avec providerId=1, mais connecté = 2
    Provider provider = new Provider(); provider.setId(1L);
    MakeupService service = MakeupService.builder().id(serviceId).provider(provider).build();

    MakeupServiceUpdateDTO dto = new MakeupServiceUpdateDTO();
    dto.setProviderId(1L); // même id
    dto.setCategoryId(2L);

    when(makeupServiceRepository.findById(serviceId)).thenReturn(Optional.of(service));

    // Simuler prestataire connecté qui n'est PAS owner du service
    SecurityUtilsTestHelper.setAuthentication(2L, "pirate@email.com", Role.PROVIDER);

    assertThrows(org.springframework.security.access.AccessDeniedException.class, () ->
        makeupServiceService.updateMakeupService(serviceId, dto)
    );
}

// Test : updateMakeupService - service inexistant
@Test
void testUpdateMakeupService_ServiceNotFound() {
    when(makeupServiceRepository.findById(99L)).thenReturn(Optional.empty());
    MakeupServiceUpdateDTO dto = new MakeupServiceUpdateDTO();
    dto.setProviderId(1L);
    dto.setCategoryId(2L);

    boolean result = makeupServiceService.updateMakeupService(99L, dto);
    assertFalse(result);
}

// Test : deleteMakeupService - succès
@Test
void testDeleteMakeupService_Success() {
    Long serviceId = 5L;
    Long providerId = 1L;

    Provider provider = new Provider(); provider.setId(providerId);
    MakeupService service = MakeupService.builder().id(serviceId).provider(provider).build();

    when(makeupServiceRepository.findById(serviceId)).thenReturn(Optional.of(service));

    boolean result = makeupServiceService.deleteMakeupService(serviceId);

    assertTrue(result);
    verify(makeupServiceRepository).delete(service);
}

// Test : deleteMakeupService - provider différent (access denied)
@Test
void testDeleteMakeupService_AccessDenied() {
    Long serviceId = 5L;
    Provider provider = new Provider(); provider.setId(1L);
    MakeupService service = MakeupService.builder().id(serviceId).provider(provider).build();

    when(makeupServiceRepository.findById(serviceId)).thenReturn(Optional.of(service));

    // Simule prestataire connecté qui n'est PAS owner
    SecurityUtilsTestHelper.setAuthentication(2L, "notme@email.com", Role.PROVIDER);

    assertThrows(org.springframework.security.access.AccessDeniedException.class, () ->
        makeupServiceService.deleteMakeupService(serviceId)
    );
}

// Test : deleteMakeupService - service inexistant
@Test
void testDeleteMakeupService_NotFound() {
    when(makeupServiceRepository.findById(999L)).thenReturn(Optional.empty());
    boolean result = makeupServiceService.deleteMakeupService(999L);
    assertFalse(result);
}

// Test : getServicesByCategory - renvoie bien une liste (mapping ok)
@Test
void testGetServicesByCategory_ReturnsList() {
    Long categoryId = 77L;
    Category category = new Category(); category.setId(categoryId);

    MakeupService service = MakeupService.builder()
            .id(1L).title("Test Service").category(category)
            .provider(new Provider())
            .description("desc")
            .duration(20)
            .price(12.5)
            .build();

    when(makeupServiceRepository.findByCategoryId(categoryId)).thenReturn(java.util.List.of(service));

    var list = makeupServiceService.getServicesByCategory(categoryId);
    assertEquals(1, list.size());
    assertEquals("Test Service", list.get(0).getTitle());
}

// Test : getAllServices - mapping
@Test
void testGetAllServices_ReturnsList() {
    MakeupService service = MakeupService.builder()
            .id(1L).title("All Svc")
            .provider(new Provider())
            .category(new Category())
            .description("desc").duration(15).price(8.0)
            .build();
    when(makeupServiceRepository.findAll()).thenReturn(java.util.List.of(service));
    var result = makeupServiceService.getAllServices();
    assertEquals(1, result.size());
    assertEquals("All Svc", result.get(0).getTitle());
}

@Test
void testUpdateMakeupService_success() {
    SecurityUtilsTestHelper.setAuthentication(1L, "pro@email.com", Role.PROVIDER);
    MakeupService old = MakeupService.builder().id(10L)
        .provider(Provider.builder().id(1L).build())
        .category(Category.builder().id(2L).build())
        .title("Old").build();
    when(makeupServiceRepository.findById(10L)).thenReturn(Optional.of(old));
    when(providerRepository.findById(1L)).thenReturn(Optional.of(old.getProvider()));
    when(categoryRepository.findById(2L)).thenReturn(Optional.of(old.getCategory()));
    MakeupServiceUpdateDTO dto = new MakeupServiceUpdateDTO();
    dto.setProviderId(1L); dto.setCategoryId(2L);
    dto.setTitle("New"); dto.setDescription("desc"); dto.setDuration(10); dto.setPrice(100.0);
    boolean result = makeupServiceService.updateMakeupService(10L, dto);
    assertTrue(result);
    verify(makeupServiceRepository).save(any());
    SecurityUtilsTestHelper.clearAuthentication();
}

@Test
void testDeleteMakeupService_success() {
    SecurityUtilsTestHelper.setAuthentication(1L, "pro@email.com", Role.PROVIDER);
    MakeupService old = MakeupService.builder().id(10L)
        .provider(Provider.builder().id(1L).build()).build();
    when(makeupServiceRepository.findById(10L)).thenReturn(Optional.of(old));
    boolean result = makeupServiceService.deleteMakeupService(10L);
    assertTrue(result);
    verify(makeupServiceRepository).delete(old);
    SecurityUtilsTestHelper.clearAuthentication();
}


}
