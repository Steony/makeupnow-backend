package com.makeupnow.backend.unit.service.mongo;

import com.makeupnow.backend.dto.review.ReviewCreateDTO;
import com.makeupnow.backend.dto.review.ReviewUpdateDTO;
import com.makeupnow.backend.model.mongo.Review;
import com.makeupnow.backend.model.mysql.enums.Role;
import com.makeupnow.backend.repository.mongo.ReviewRepository;
import com.makeupnow.backend.repository.mysql.UserRepository;
import com.makeupnow.backend.service.mongo.ReviewService;
import com.makeupnow.backend.service.mysql.UserActionLogService;
import com.makeupnow.backend.unit.security.SecurityUtilsTestHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserActionLogService userActionLogService;

    @InjectMocks
    private ReviewService reviewService;

    @Mock
private UserRepository userRepository;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    
    @Test
    void testCreateReview() {
    // Simuler un utilisateur connecté avec un rôle CLIENT
    SecurityUtilsTestHelper.setAuthentication(1L, "test@example.com", Role.CLIENT);

    // Préparation
    ReviewCreateDTO dto = new ReviewCreateDTO();
    dto.setCustomerId(1L);
    dto.setProviderId(2L);
    dto.setMakeupServiceId(3L);
    dto.setRating(5);
    dto.setComment("Super prestation!");

    Review savedReview = Review.builder()
            .id("abc123")
            .customerId(1L)
            .providerId(2L)
            .makeupServiceId(3L)
            .rating(5)
            .comment("Super prestation!")
            .build();

    // Mocks
    when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

    // Appel
    var createdDTO = reviewService.createReview(dto);

    // Vérifications
    assertNotNull(createdDTO);
    assertEquals(5, createdDTO.getRating());
    assertEquals("Super prestation!", createdDTO.getComment());

    verify(reviewRepository).save(any(Review.class));

    // Adapter ici
    verify(userActionLogService).logActionByUserId(
        eq(1L),
        eq("Création d'avis"),
        contains("Avis créé pour le prestataire ID 2 et service ID 3")
);


    // Nettoyer
    SecurityUtilsTestHelper.clearAuthentication();
}


    @Test
    void testGetReviewsByProvider() {
        // 1️⃣ Simuler un utilisateur connecté (pas indispensable ici, mais bonne pratique)
        SecurityUtilsTestHelper.setAuthentication(1L, "test@example.com", Role.CLIENT);

        Review review1 = Review.builder().id("1").providerId(2L).rating(5).comment("Parfait!").build();
        Review review2 = Review.builder().id("2").providerId(2L).rating(4).comment("Bien!").build();

        when(reviewRepository.findByProviderId(2L)).thenReturn(Arrays.asList(review1, review2));

        // Appel
        var reviewDTOs = reviewService.getReviewsByProvider(2L);

        // Vérifications
        assertEquals(2, reviewDTOs.size());
        assertEquals(5, reviewDTOs.get(0).getRating());
        verify(reviewRepository).findByProviderId(2L);

        // 3️⃣ Nettoyer le contexte de sécurité
        SecurityUtilsTestHelper.clearAuthentication();
    }

@Test
void testCreateReview_accessDeniedForOtherCustomer() {
    // Simule un user connecté avec un autre ID que celui de la DTO
    SecurityUtilsTestHelper.setAuthentication(42L, "intrus@email.com", Role.CLIENT);
    ReviewCreateDTO dto = new ReviewCreateDTO();
    dto.setCustomerId(1L); // pas le même que le connecté !
    dto.setProviderId(2L); dto.setMakeupServiceId(3L);
    dto.setRating(5); dto.setComment("OK");
    assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> {
        reviewService.createReview(dto);
    });
    SecurityUtilsTestHelper.clearAuthentication();
}

@Test
void testUpdateReview_success() {
    SecurityUtilsTestHelper.setAuthentication(1L, "client@email.com", Role.CLIENT);
    Review review = Review.builder().id("r1").customerId(1L).providerId(2L).build();
    when(reviewRepository.findById("r1")).thenReturn(java.util.Optional.of(review));
    ReviewUpdateDTO dto = new ReviewUpdateDTO();
    dto.setRating(4); dto.setComment("edit");
    when(reviewRepository.save(any())).thenReturn(review);

    boolean result = reviewService.updateReview("r1", dto);
    assertTrue(result);
    verify(userActionLogService).logActionByUserId(eq(1L), eq("Modification d'avis"), contains("modifié par"));
    SecurityUtilsTestHelper.clearAuthentication();
}

@Test
void testUpdateReview_accessDeniedForOtherCustomer() {
    SecurityUtilsTestHelper.setAuthentication(2L, "other@email.com", Role.CLIENT);
    Review review = Review.builder().id("r1").customerId(1L).providerId(2L).build();
    when(reviewRepository.findById("r1")).thenReturn(java.util.Optional.of(review));
    ReviewUpdateDTO dto = new ReviewUpdateDTO();
    dto.setRating(4); dto.setComment("edit");
    assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> {
        reviewService.updateReview("r1", dto);
    });
    SecurityUtilsTestHelper.clearAuthentication();
}

@Test
void testUpdateReview_reviewNotFound() {
    SecurityUtilsTestHelper.setAuthentication(1L, "client@email.com", Role.CLIENT);
    when(reviewRepository.findById("r999")).thenReturn(java.util.Optional.empty());
    ReviewUpdateDTO dto = new ReviewUpdateDTO();
    assertThrows(com.makeupnow.backend.exception.ResourceNotFoundException.class, () -> {
        reviewService.updateReview("r999", dto);
    });
    SecurityUtilsTestHelper.clearAuthentication();
}

@Test
void testDeleteReview_success() {
    Review review = Review.builder().id("r1").providerId(2L).customerId(1L).build();
    when(reviewRepository.findById("r1")).thenReturn(java.util.Optional.of(review));
    boolean result = reviewService.deleteReview(99L, "r1");
    assertTrue(result);
    verify(reviewRepository).deleteById("r1");
    verify(userActionLogService).logActionByUserId(eq(99L), eq("Suppression d'avis"), contains("Avis supprimé"));
}

@Test
void testDeleteReview_reviewNotFound() {
    when(reviewRepository.findById("r404")).thenReturn(java.util.Optional.empty());
    assertThrows(com.makeupnow.backend.exception.ResourceNotFoundException.class, () -> {
        reviewService.deleteReview(99L, "r404");
    });
}

@Test
void testGetReviewsByCustomer() {
    Review r = Review.builder().id("1").customerId(1L).build();
    when(reviewRepository.findByCustomerId(1L)).thenReturn(java.util.List.of(r));
    var res = reviewService.getReviewsByCustomer(1L);
    assertEquals(1, res.size());
}

@Test
void testGetAllReviews() {
    when(reviewRepository.findAll()).thenReturn(java.util.List.of(
        Review.builder().id("1").build(), Review.builder().id("2").build()
    ));
    var res = reviewService.getAllReviews();
    assertEquals(2, res.size());
}


}
