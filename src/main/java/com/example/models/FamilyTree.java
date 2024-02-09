package com.example.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("family_tree")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FamilyTree {
    @Id
    private String id;
    private String name;
    private String description;

}