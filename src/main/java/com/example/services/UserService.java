package com.example.services;

import com.example.dto.UserDTO;
import com.example.dto.mappers.UserMapper;
import com.example.models.FamilyTree;
import com.example.models.Person;
import com.example.models.User;
import com.example.repositories.FamilyTreeRepository;
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
    private final FamilyTreeRepository familyTreeRepository;
    private final GedcomService gedcomService;

    public List<Person> getAllByFamilyTreeId(String familyTreeId) {
        return personRepository.getAllByFamilyTreeId(familyTreeId);
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

    public String exportFamilyTree(String username, String familyTreeId) {
        List<Person> familyTree = getAllByFamilyTreeId(familyTreeId);

        return gedcomService.createGedcom(username, familyTree);
    }

    public FamilyTree createFamilyTree(String userId, FamilyTree familyTree) {
        User user = userRepository.getById(userId);
        familyTreeRepository.save(familyTree);

        user.addFamilyTree(familyTree.getId());
        userRepository.save(user);

        return familyTree;
    }
}
