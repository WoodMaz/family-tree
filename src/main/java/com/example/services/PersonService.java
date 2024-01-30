package com.example.services;

import com.example.models.Person;
import com.example.repositories.PersonRepository;
import com.example.utils.ThrowingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonService {
    private final PersonRepository personRepository;

    public Person getById(String personId) throws NoSuchElementException {
        return personRepository.findById(personId)
                .orElseThrow(ThrowingUtil.personNotFound(personId));
    }

    public List<Person> getChildren(String personId) {
        return personRepository.getChildren(personId);
    }

    public List<Person> getSpouses(String personId) {
        return personRepository.getSpouses(personId);
    }

    public void updatePerson(Person person) {
        personRepository.save(person);
        log.info("{} {} has been updated", person.getName(), person.getSurname());
    }

    public void deleteById(String personId) {
        Person person = getById(personId);
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


    public void add(String userId, Person person) {
        person.setUserId(userId);

        personRepository.save(person);

        log.info("{} {} has been added", person.getName(), person.getSurname());
    }

    public void addSpouse(String userId, String personId, Person spouse) throws NoSuchElementException {
        Person person = getById(personId);

        spouse.addSpouse(personId);

        addNewPersonWithRelation(userId, spouse, person, person::addSpouse);
    }

    public void addParent(String userId, String personId, Person parent) throws NoSuchElementException {
        Person person = getById(personId);

        switch (parent.getSex()) {
            case FEMALE -> addNewPersonWithRelation(userId, parent, person, person::setMotherId);
            case MALE -> addNewPersonWithRelation(userId, parent, person, person::setFatherId);
        }
    }

    private void addNewPersonWithRelation(String userId, Person newPerson, Person oldPerson, Consumer<String> relationProperty) {
        newPerson.setUserId(userId);
        personRepository.save(newPerson);

        relationProperty.accept(newPerson.getId());
        personRepository.save(oldPerson);

        log.info("{} {} has been added", newPerson.getName(), newPerson.getSurname());
    }

    public void bondSpouses(String spouse1Id, String spouse2Id) {
        Person spouse1 = getById(spouse1Id);
        Person spouse2 = getById(spouse2Id);

        spouse1.addSpouse(spouse2Id);
        spouse2.addSpouse(spouse1Id);

        personRepository.save(spouse1);
        personRepository.save(spouse2);

        log.info("{} {} and {} {} has been joined in marriage", spouse1.getName(), spouse1.getSurname(), spouse2.getName(), spouse2.getSurname());
    }

    public void bondChildWithParent(String childId, String parentId) {
        Person child = getById(childId);
        Person parent = getById(parentId);

        switch (parent.getSex()) {
            case FEMALE -> child.setMotherId(parentId);
            case MALE -> child.setFatherId(parentId);
        }
        personRepository.save(child);

        log.info("{} {} has been set as a parent of {} {}", parent.getName(), parent.getSurname(), child.getName(), child.getSurname());
    }

    public void undoBondSpouses(String spouse1Id, String spouse2Id) {
        Person spouse1 = getById(spouse1Id);
        Person spouse2 = getById(spouse2Id);

        spouse1.removeSpouse(spouse2Id);
        spouse2.removeSpouse(spouse1Id);

        personRepository.save(spouse1);
        personRepository.save(spouse2);

        log.info("Marriage of {} {} and {} {} has been cancelled", spouse1.getName(), spouse1.getSurname(), spouse2.getName(), spouse2.getSurname());
    }

    public void undoBondChildWithParent(String childId, String parentId) {
        Person child = getById(childId);
        Person parent = getById(parentId);

        switch (parent.getSex()) {
            case FEMALE -> child.setMotherId("");
            case MALE -> child.setFatherId("");
        }

        personRepository.save(child);

        log.info("{} {} is no longer a parent of {} {}", parent.getName(), parent.getSurname(), child.getName(), child.getSurname());
    }
}
