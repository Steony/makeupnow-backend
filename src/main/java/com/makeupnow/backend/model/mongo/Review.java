package com.makeupnow.backend.model.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    private String id;

    private Long customerId;
    private Long providerId;

    private int rating; // ex: de 1 à 5
    private String comment;

    private LocalDateTime dateComment;

    private String bookingId; // lien avec la réservation concernée 

}
