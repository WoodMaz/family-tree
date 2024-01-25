package com.example.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Document("person")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person {
    @Id
    private String id;

    private String name;
    private String surname;
    private LocalDate birthDate;
    private LocalDate deathDate;

    private String motherId;
    private String fatherId;
    private String spouseId;

    private String note;

    private String userId;
}
