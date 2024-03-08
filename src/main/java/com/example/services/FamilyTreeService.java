package com.example.services;

import com.example.config.security.JwtService;
import com.example.models.FamilyTree;
import com.example.models.Person;
import com.example.models.User;
import com.example.repositories.FamilyTreeRepository;
import com.example.services.gedcom.GedcomService;
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
    private final JwtService jwtService;
    private final UserService userService;
    private final PersonService personService;
    private final GedcomService gedcomService;
    private final AccessService accessService;
    private final FamilyTreeRepository familyTreeRepository;

    public FamilyTree createFamilyTree(FamilyTree familyTree, String token) {
        User user = userService.getByToken(token);

        familyTreeRepository.save(familyTree);

        user.addFamilyTree(familyTree.getId());
        userService.update(user);

        return familyTree;
    }

    public void delete(String id, String token) throws AuthenticationException {
        accessService.checkAccessToFamilyTree(id, token);

        User user = userService.getByToken(token);
        user.getFamilyTreeIds().remove(id);
        userService.update(user);

        List<User> users = userService.findAllByFamilyTree(id);
        if (users.isEmpty()) {
            familyTreeRepository.deleteById(id);
            personService.deleteAll(personService.getAllByFamilyTreeId(id));
        }
    }

    public List<Person> getAllFamilyTreeMembers(String familyTreeId, String token) throws AuthenticationException {
        accessService.checkAccessToFamilyTree(familyTreeId, token);
        return personService.getAllByFamilyTreeId(familyTreeId);
    }

    public String exportFamilyTree(String familyTreeId, String token) throws AuthenticationException {
        String username = jwtService.extractUsername(token);
        List<Person> familyTree = getAllFamilyTreeMembers(familyTreeId, token);

        return gedcomService.createGedcom(username, familyTree);
    }

    @Transactional
    //TODO: refactor
    public FamilyTree mergeFamilyTrees(List<String> familyTreeIds, FamilyTree mergedTree, String token) throws AuthenticationException {
        List<Person> allPeople = new ArrayList<>();

        familyTreeRepository.save(mergedTree);
        String mergedTreeId = mergedTree.getId();
        userService.addFamilyTreeId(mergedTreeId, token);

        for (String id : familyTreeIds) {
            allPeople.addAll(getAllFamilyTreeMembers(id, token));
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
                                Person newPerson = personService.copyToOtherFamilyTree(person, mergedTreeId);
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

        personService.saveAll(finalList);

        return mergedTree;
    }

    @Getter
    @AllArgsConstructor
    private static class Marriage {
        private String personOldId;
        private String personNewId;
        private Set<String> spouseOldIds;
    }













    @Deprecated
    //TODO: needed for test only to see finalList, delete
    public List<Person> mergeFamilyTreesForTest(List<String> familyTreeIds, FamilyTree mergedTree, String token) throws AuthenticationException {
        List<Person> allPeople = new ArrayList<>();

        familyTreeRepository.save(mergedTree);
        String mergedTreeId = mergedTree.getId();
        userService.addFamilyTreeId(mergedTreeId, token);

        for (String id : familyTreeIds) {
            allPeople.addAll(getAllFamilyTreeMembers(id, token));
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
                                Person newPerson = personService.copyToOtherFamilyTree(person, mergedTreeId);
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

        personService.saveAll(finalList);

        return finalList;
    }
}
