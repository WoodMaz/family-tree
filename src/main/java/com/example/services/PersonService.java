package com.example.services;

import com.example.models.Person;
import com.example.repositories.PersonRepository;
import com.example.models.User;
import com.example.repositories.UserRepository;
import com.example.utils.ThrowingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonService {
    private final PersonRepository personRepository;
    private final UserRepository userRepository;
    public List<Person> getAll() {
        return personRepository.findAll();
    }

    public void add(String username, Person person) {
        User user = userRepository.getByUsername(username).orElseThrow(ThrowingUtil.userNotFound(username));
//        user.getFamilyTree().add(person);
//
//        userRepository.save(user);
        person.setUserId(user.getId());
        user.getFamilyTree().add(person);
        personRepository.save(person);

        log.info("{} {} has been added", person.getName(), person.getSurname());
    }

    public Person getById(String personId) throws Exception {
        return personRepository.findById(personId).orElseThrow(() ->
                new Exception("Person with ID: {personId} does not exist"));
    }

    public void addSpouse(String personId, Person spouse) throws Exception {

        Person person = personRepository.findById(personId).orElseThrow(() ->
                new Exception("Person with ID: {personId} does not exist"));

        spouse.setSpouseId(personId);
        personRepository.save(spouse);

        person.setSpouseId(spouse.getId());
        personRepository.save(person);
    }

    public void addMother(String personId, Person mother) throws Exception {
        Person person = personRepository.findById(personId).orElseThrow(() ->
                new Exception("Person with ID: {personId} does not exist"));

        personRepository.save(mother);

        person.setMotherId(mother.getId());
        personRepository.save(person);
    }

    public void addFather(String username, String personId, Person father) throws Exception {
        User user = userRepository.getByUsername(username).orElseThrow(ThrowingUtil.userNotFound(username));

        Person person = user.getFamilyTree()
                .stream()
                .filter(p -> p.getId().equals(personId))
                .findFirst()
                .orElseThrow(() -> new Exception("Person with ID: {personId} does not exist"));

        user.getFamilyTree().add(father);
        person.setFatherId(father.getId());
        userRepository.save(user);
    }
}
