package com.example.services;

import com.example.dto.UserDTO;
import com.example.dto.mappers.UserMapper;
import com.example.models.Person;
import com.example.repositories.PersonRepository;
import com.example.repositories.UserRepository;
import com.example.services.gedcom.GedcomService;
import com.example.utils.ThrowingUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PersonRepository personRepository;
    private final GedcomService gedcomService;

    public List<Person> getFamilyTree(String userId) {
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


    public String exportFamilyTree(String id) {
        List<Person> familyTree = getFamilyTree(id);
        String username = getById(id).getUsername();

        return gedcomService.createGedcom(familyTree, username);
    }
}
