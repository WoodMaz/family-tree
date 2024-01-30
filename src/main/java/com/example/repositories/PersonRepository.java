package com.example.repositories;

import com.example.models.Person;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PersonRepository extends MongoRepository<Person, String> {
    Set<Person> getAllByUserId(String userId);

    @Query(
            "{ "
                + "$or: ["
                    + "{ 'fatherId': [?0] }, "
                    + "{ 'motherId': [?0] } "
                + "]"
            + "}"
    )
    List<Person> getChildren(String personId);

    @Query(
            "{ "
                + "'spouseIds': { $all: [?0] } "
            + "}"
    )
    List<Person> getSpouses(String personId);
}