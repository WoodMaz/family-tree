package com.example.controllers;

import com.example.config.security.JwtAuthenticationFilter;
import com.example.config.security.JwtService;
import com.example.models.Person;
import com.example.services.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/person")
public class PersonController {
    private final PersonService personService;
    private final JwtService jwtService;

    @PostMapping("/add")
    public ResponseEntity<Void> add(
            @RequestBody Person person,
            @RequestHeader(JwtAuthenticationFilter.AUTH_HEADER) String token) {

        String username = jwtService.extractUsername(token);
        personService.add(username, person);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("{id}/add-spouse")
    public ResponseEntity<Void> addSpouse(@PathVariable("id") String personId, @RequestBody Person spouse) {
        try {
            personService.addSpouse(personId, spouse);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("{id}/add-mother")
    public ResponseEntity<Void> addMother(@PathVariable("id") String personId, @RequestBody Person mother) {
        try {
            personService.addMother(personId, mother);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("{id}/add-father")
    public ResponseEntity<Void> addFather(
            @PathVariable("id") String personId,
            @RequestBody Person father,
            @RequestHeader(JwtAuthenticationFilter.AUTH_HEADER) String token) {

        String username = jwtService.extractUsername(token);

        try {
            personService.addFather(username, personId, father);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/show-all")
    public ResponseEntity<List<Person>> getAll() {
        return new ResponseEntity<>(personService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/show/{id}")
    public ResponseEntity<Person> getPerson(@PathVariable String personId) {
        try {
            return new ResponseEntity<>(personService.getById(personId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
