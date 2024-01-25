package com.example.services;

import com.example.dto.UserDTO;
import com.example.dto.mappers.UserMapper;
import com.example.models.Person;
import com.example.models.User;
import com.example.repositories.PersonRepository;
import com.example.repositories.UserRepository;
import com.example.utils.ThrowingUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PersonRepository personRepository;

    public Set<Person> getFamilyTree(String userId) {
        return personRepository.getAllByUserId(userId);
    }

    public UserDTO getById(String id) {
        return UserMapper.asDTO(userRepository.getById(id));
    }

    public UserDTO getByUsername(String username) {
        return UserMapper.asDTO(
                userRepository
                        .getByUsername(username)
                        .orElseThrow(ThrowingUtil.userNotFound(username))
        );
    }

    public void addPerson(String userId, Person person) {
        User user = userRepository.getById(userId);
        user.getFamilyTree().add(person);
        userRepository.save(user);
    }



}
