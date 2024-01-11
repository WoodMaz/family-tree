package com.example.user;

import com.example.person.Person;
import com.example.utils.ThrowingUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
public class UserService {
    private UserRepository userRepository;

    public Set<Person> getFamilyTree(String userId) {
        return userRepository.getFamilyTreById(userId);
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
