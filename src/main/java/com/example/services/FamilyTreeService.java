package com.example.services;

import com.example.models.FamilyTree;
import com.example.models.Person;
import com.example.models.User;
import com.example.repositories.FamilyTreeRepository;
import com.example.repositories.PersonRepository;
import com.example.repositories.UserRepository;
import com.example.utils.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FamilyTreeService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final AccessService accessService;
    private final PersonService personService;
    private final PersonRepository personRepository;
    private final FamilyTreeRepository familyTreeRepository;

    public void delete(String id, String token) throws AuthenticationException {
        accessService.checkAccessToFamilyTree(id, token);

        User user = userService.getByToken(token);
        user.getFamilyTreeIds().remove(id);
        userRepository.save(user);

        List<User> users = userRepository.findAllByFamilyTree(id);
        if (users.isEmpty()) {
            familyTreeRepository.deleteById(id);
            personRepository.deleteAll(personRepository.getAllByFamilyTreeId(id));
        }
    }

    @Transactional
    //TODO: refactor
    public FamilyTree mergeFamilyTrees(List<String> familyTreeIds, FamilyTree mergedTree, String token) throws AuthenticationException {
        List<Person> allPeople = new ArrayList<>();

        mergedTree = familyTreeRepository.save(mergedTree);
        String mergedTreeId = mergedTree.getId();
        userService.addFamilyTree(mergedTreeId, token);

        for (String id : familyTreeIds) {
            allPeople.addAll(personService.getAllByFamilyTreeId(id, token));
        }

        Map<String, String> oldToNewId = new HashMap<>();
        List<Marriage> marriages = new ArrayList<>();
        List<Person> finalList = new ArrayList<>();

        for (Person person : allPeople) {
            CommonUtils.findInCollection(finalList, person,
                            Person::getName,
                            Person::getSurname,
                            Person::getSex,
                            Person::getBirthDate,
                            Person::getDeathDate)
                    .ifPresentOrElse(original -> {
                                if (original.getMotherId() == null) {
                                    original.setMotherId(person.getMotherId());
                                }
                                if (original.getFatherId() == null) {
                                    original.setFatherId(person.getFatherId());
                                }
                                original.getSpouseIds().addAll(person.getSpouseIds());
                                oldToNewId.put(person.getId(), original.getId());
                            }, () -> {
                                Person newPerson = new Person(person, mergedTreeId);
                                personRepository.save(newPerson);
                                finalList.add(newPerson);
                                oldToNewId.put(person.getId(), newPerson.getId());
                                marriages.add(new Marriage(person.getId(), newPerson.getId(), person.getSpouseIds()));
                            }
                    );
        }

        if (finalList.isEmpty()) {
            throw new IllegalStateException("Trees have 0 jointed people");
        }

        for (Person person : finalList) {
            if (oldToNewId.containsKey(person.getMotherId())) {
                person.setMotherId(oldToNewId.get(person.getMotherId()));
            }
            if (oldToNewId.containsKey(person.getFatherId())) {
                person.setFatherId(oldToNewId.get(person.getFatherId()));
            }

            List<String> spouseIdsToAdd = new ArrayList<>();
            List<String> spouseIdsToRemove = new ArrayList<>();
            for (String spouseId : person.getSpouseIds()) {
                if (oldToNewId.containsKey(spouseId)) {
                    spouseIdsToAdd.add(oldToNewId.get(spouseId));
                    spouseIdsToRemove.add(spouseId);
                }
            }
            person.getSpouseIds().removeAll(spouseIdsToRemove);
            person.getSpouseIds().addAll(spouseIdsToAdd);

            for (Marriage marriage : marriages) {
                for (String oldSpouseId: marriage.getSpouseOldIds()) {
                    if (oldToNewId.get(oldSpouseId).equals(person.getId())) {
                        person.getSpouseIds().add(marriage.getPersonNewId());
                        break;
                    }
                }
            }
        }

        personRepository.saveAll(finalList);

        return mergedTree;
    }

    @Getter
    @AllArgsConstructor
    private static class Marriage {
        private String personOldId;
        private String personNewId;
        private Set<String> spouseOldIds;
    }
}
