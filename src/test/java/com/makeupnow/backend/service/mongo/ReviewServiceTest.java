package com.makeupnow.backend.service.mongo;

import com.makeupnow.backend.dto.review.ReviewCreateDTO;
import com.makeupnow.backend.model.mongo.Review;
import com.makeupnow.backend.repository.mongo.ReviewRepository;
import com.makeupnow.backend.service.mysql.UserActionLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import org.mockito.MockitoAnnotations;

public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserActionLogService userActionLogService;

    @InjectMocks
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
void testCreateReview() {
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
    verify(userActionLogService).logActionByUserId(1L, "Création d'avis",
            "Avis laissé pour le prestataire 2L");
}


   @Test
void testGetReviewsByProvider() {
    Review review1 = Review.builder().id("1").providerId(2L).rating(5).comment("Parfait!").build();
    Review review2 = Review.builder().id("2").providerId(2L).rating(4).comment("Bien!").build();

    when(reviewRepository.findByProviderId(2L)).thenReturn(Arrays.asList(review1, review2));

    // Appel
    var reviewDTOs = reviewService.getReviewsByProvider(2L);

    // Vérifications
    assertEquals(2, reviewDTOs.size());
    assertEquals(5, reviewDTOs.get(0).getRating());
    verify(reviewRepository).findByProviderId(2L);
}

}
