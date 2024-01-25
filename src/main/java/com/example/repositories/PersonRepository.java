package com.example.repositories;

import com.example.models.Person;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;


@Repository
public interface PersonRepository extends MongoRepository<Person, String> {
    Set<Person> getAllByUserId(String userId);

}