package com.example.services;

import com.example.config.security.JwtService;
import com.example.models.Person;
import com.example.repositories.PersonRepository;
import com.example.services.gedcom.GedcomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonService {
    private final AccessService accessService;
    private final PersonRepository personRepository;
    private final GedcomService gedcomService;
    private final JwtService jwtService;

    public Person getById(String personId, String token) throws AuthenticationException {
        return accessService.getPersonIfHasAccess(personId, token);
    }

    public List<Person> getAllByFamilyTreeId(String familyTreeId, String token) throws AuthenticationException {
        accessService.checkAccessToFamilyTree(familyTreeId, token);
        return personRepository.getAllByFamilyTreeId(familyTreeId);
    }

    public String exportFamilyTree(String familyTreeId, String token) throws AuthenticationException {
        String username = jwtService.extractUsername(token);
        List<Person> familyTree = getAllByFamilyTreeId(familyTreeId, token);

        return gedcomService.createGedcom(username, familyTree);
    }

    public List<Person> getChildren(String personId, String token) throws AuthenticationException {
        accessService.getPersonIfHasAccess(personId, token);
        return personRepository.getChildren(personId);
    }

    public List<Person> getSpouses(String personId, String token) throws AuthenticationException {
        accessService.getPersonIfHasAccess(personId, token);
        return personRepository.getSpouses(personId);
    }

    public void updatePerson(Person person, String token) throws AuthenticationException {
        accessService.checkAccessToFamilyTree(person.getFamilyTreeId(), token);
        personRepository.save(person);
        log.info("{} {} has been updated", person.getName(), person.getSurname());
    }

    public void deleteById(String personId, String token) throws AuthenticationException {
        Person person = accessService.getPersonIfHasAccess(personId, token);

        List<Person> children = personRepository.getChildren(personId);

        switch (person.getSex()) {
            case FEMALE -> children.forEach(child -> child.setMotherId(""));
            case MALE -> children.forEach(child -> child.setFatherId(""));
        }

        personRepository.saveAll(children);

        List<Person> spouses = personRepository.getSpouses(personId);
        spouses.forEach(spouse -> spouse.removeSpouse(personId));
        personRepository.saveAll(spouses);

        personRepository.deleteById(personId);
        log.info("{} {} has been deleted", person.getName(), person.getSurname());
    }


    public Person add(String familyTreeId, Person person, String token) throws AuthenticationException {
        accessService.checkAccessToFamilyTree(familyTreeId, token);

        person.setFamilyTreeId(familyTreeId);
        personRepository.save(person);

        log.info("{} {} has been added", person.getName(), person.getSurname());

        return person;
    }

    public Person addSpouse(String familyTreeId, String personId, Person spouse, String token) throws AuthenticationException {
        Person person = accessService.getPersonIfHasAccess(personId, token);

        spouse.addSpouse(personId);

        addNewPersonWithRelation(familyTreeId, spouse, person, person::addSpouse);

        return spouse;
    }

    public Person addParent(String familyTreeId, String personId, Person parent, String token) throws AuthenticationException {
        Person person = accessService.getPersonIfHasAccess(personId, token);

        switch (parent.getSex()) {
            case FEMALE -> addNewPersonWithRelation(familyTreeId, parent, person, person::setMotherId);
            case MALE -> addNewPersonWithRelation(familyTreeId, parent, person, person::setFatherId);
        }

        return parent;
    }

    private void addNewPersonWithRelation(String familyTreeId, Person newPerson, Person oldPerson, Consumer<String> relationProperty) {
        newPerson.setFamilyTreeId(familyTreeId);
        personRepository.save(newPerson);

        relationProperty.accept(newPerson.getId());
        personRepository.save(oldPerson);

        log.info("{} {} has been added", newPerson.getName(), newPerson.getSurname());
    }

    public void bondSpouses(String spouse1Id, String spouse2Id, String token) throws AuthenticationException {
        Person spouse1 = accessService.getPersonIfHasAccess(spouse1Id, token);
        Person spouse2 = accessService.getPersonIfHasAccess(spouse2Id, token);

        spouse1.addSpouse(spouse2Id);
        spouse2.addSpouse(spouse1Id);

        personRepository.save(spouse1);
        personRepository.save(spouse2);

        log.info("{} {} and {} {} has been joined in marriage", spouse1.getName(), spouse1.getSurname(), spouse2.getName(), spouse2.getSurname());
    }

    public void bondChildWithParent(String childId, String parentId, String token) throws AuthenticationException {
        Person child = accessService.getPersonIfHasAccess(childId, token);
        Person parent = accessService.getPersonIfHasAccess(parentId, token);

        switch (parent.getSex()) {
            case FEMALE -> child.setMotherId(parentId);
            case MALE -> child.setFatherId(parentId);
        }
        personRepository.save(child);

        log.info("{} {} has been set as a parent of {} {}", parent.getName(), parent.getSurname(), child.getName(), child.getSurname());
    }

    public void undoBondSpouses(String spouse1Id, String spouse2Id, String token) throws AuthenticationException {
        Person spouse1 = accessService.getPersonIfHasAccess(spouse1Id, token);
        Person spouse2 = accessService.getPersonIfHasAccess(spouse2Id, token);

        spouse1.removeSpouse(spouse2Id);
        spouse2.removeSpouse(spouse1Id);

        personRepository.save(spouse1);
        personRepository.save(spouse2);

        log.info("Marriage of {} {} and {} {} has been cancelled", spouse1.getName(), spouse1.getSurname(), spouse2.getName(), spouse2.getSurname());
    }

    public void undoBondChildWithParent(String childId, String parentId, String token) throws AuthenticationException {
        Person child = accessService.getPersonIfHasAccess(childId, token);
        Person parent = accessService.getPersonIfHasAccess(parentId, token);

        switch (parent.getSex()) {
            case FEMALE -> child.setMotherId("");
            case MALE -> child.setFatherId("");
        }

        personRepository.save(child);

        log.info("{} {} is no longer a parent of {} {}", parent.getName(), parent.getSurname(), child.getName(), child.getSurname());
    }
}
