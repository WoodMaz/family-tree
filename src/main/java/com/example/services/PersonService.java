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

    public void addMother(String userId, String personId, Person mother) throws NoSuchElementException {
        Person person = getById(personId);

        addNewPersonWithRelation(userId, mother, person, person::setMotherId);
    }

    public void addFather(String userId, String personId, Person father) throws NoSuchElementException {
        Person person = getById(personId);

        addNewPersonWithRelation(userId, father, person, person::setFatherId);
    }

    private void addNewPersonWithRelation(String userId, Person newPerson, Person oldPerson, Consumer<String> relationProperty) {
        newPerson.setUserId(userId);
        personRepository.save(newPerson);

        relationProperty.accept(newPerson.getId());
        personRepository.save(oldPerson);

        log.info("{} {} has been added", newPerson.getName(), newPerson.getSurname());
    }
}
