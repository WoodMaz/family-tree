package com.example.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

//TODO: handle case when someone has more than one spouse

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

    private Set<String> parentIds = new HashSet<>();
    private Set<String> spouseIds = new HashSet<>();

    private String note;

    private String userId;

    public void addParent(String parentId) {
        this.parentIds.add(parentId);
    }

    public void removeParent(String parentId) {
        this.parentIds.remove(parentId);
    }

    public void addSpouse(String spouseId) {
        this.spouseIds.add(spouseId);
    }

    public void removeSpouse(String spouseId) {
        this.spouseIds.remove(spouseId);
    }
}
