package com.example.user;

import com.example.person.Person;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Set<Person> getFamilyTreById(String userId);

    User getById(String userId);

    Optional<User> getByUsername(String username);

}
