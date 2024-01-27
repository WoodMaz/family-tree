package com.example.services;

import com.example.models.Person;
import com.example.repositories.PersonRepository;
import com.example.utils.ThrowingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

        addNewPersonWithRelation(userId, parent, person, person::addParent);
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

        child.addParent(parentId);

        personRepository.save(child);

        log.info("{} {} has been set as a parent of {} {}", parent.getName(), parent.getSurname(), child.getName(), child.getSurname());
    }
}
