package com.example.repositories;

import com.example.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> getByUsername(String username);

    @Query(
            "{ "
                + "'familyTreeIds': { $all: [?0] } "
            + "}"
    )
    List<User> findAllByFamilyTree(String id);
}
