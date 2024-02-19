package com.example.services;

import com.example.models.Person;
import com.example.models.User;
import com.example.repositories.PersonRepository;
import com.example.utils.ThrowingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;

@Service
@RequiredArgsConstructor
public class AccessService {
    private final UserService userService;
    private final PersonRepository personRepository;

    public void checkAccessToFamilyTree(String familyTreeId, String token) throws AuthenticationException {
        User user = userService.getByToken(token);

        if (!user.getFamilyTreeIds().contains(familyTreeId)) {
            throw new AuthenticationException("User have no access to this family tree");
        }
    }

    public Person getPersonIfHasAccess(String personId, String token) throws AuthenticationException {
        User user = userService.getByToken(token);

        Person person = personRepository.findById(personId)
                .orElseThrow(ThrowingUtil.personNotFound(personId));

        if (!user.getFamilyTreeIds().contains(person.getFamilyTreeId())) {
            throw new AuthenticationException("User have no access to this family tree");
        }

        return person;
    }
}
