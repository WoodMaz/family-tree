package com.example.models;

import com.example.enums.Sex;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
    private Sex sex;
    private LocalDate birthDate;
    private LocalDate deathDate;

    private String motherId;
    private String fatherId;
    private Set<String> spouseIds = new HashSet<>();

    private String note;

    private String userId;


    public void addSpouse(String spouseId) {
        this.spouseIds.add(spouseId);
    }

    public void removeSpouse(String spouseId) {
        this.spouseIds.remove(spouseId);
    }
}
