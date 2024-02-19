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

    private String familyTreeId;

    public Person(Person src, String familyTreeId) {
        this.name = src.name;
        this.surname = src.surname;
        this.sex = src.sex;
        this.birthDate = src.birthDate;
        this.deathDate = src.deathDate;
        this.motherId = src.motherId;
        this.fatherId = src.fatherId;
        this.spouseIds.addAll(src.spouseIds);
        this.note = src.note;
        this.familyTreeId = familyTreeId;
    }

    public void addSpouse(String spouseId) {
        this.spouseIds.add(spouseId);
    }

    public void removeSpouse(String spouseId) {
        this.spouseIds.remove(spouseId);
    }
}
