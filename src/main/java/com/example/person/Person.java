package com.example.person;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;


//TODO: PERHAPS PERSON BE ANOTHER COLLECTION WITH ADDITIONAL FIELD 'userId'
//TODO: NOW ID IS NOT GENERATED

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
}
