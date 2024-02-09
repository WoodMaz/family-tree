package com.example.repositories;

import com.example.models.FamilyTree;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyTreeRepository extends MongoRepository<FamilyTree, String> {
    FamilyTree getById(String familyTreeId);
}
