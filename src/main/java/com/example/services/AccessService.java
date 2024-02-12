package com.example.services;

import com.example.config.security.JwtService;
import com.example.models.Person;
import com.example.models.User;
import com.example.repositories.PersonRepository;
import com.example.repositories.UserRepository;
import com.example.utils.ThrowingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;

@Service
@RequiredArgsConstructor
public class AccessService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PersonRepository personRepository;

    public void checkAccessToFamilyTree(String familyTreeId, String token) throws AuthenticationException {
        String username = jwtService.extractUsername(token);
        User user = userRepository.getByUsername(username)
                .orElseThrow(ThrowingUtil.userNotFound(username));

        if (!user.getFamilyTreeIds().contains(familyTreeId)) {
            throw new AuthenticationException("User have no access to this family tree");
        }
    }

    public Person getPersonIfHasAccess(String personId, String token) throws AuthenticationException {
        String username = jwtService.extractUsername(token);
        User user = userRepository.getByUsername(username)
                .orElseThrow(ThrowingUtil.userNotFound(username));

        Person person = personRepository.findById(personId)
                .orElseThrow(ThrowingUtil.personNotFound(personId));

        if (!user.getFamilyTreeIds().contains(person.getFamilyTreeId())) {
            throw new AuthenticationException("User have no access to this family tree");
        }

        return person;
    }
}
