package com.example.controllers;

import com.example.models.Person;
import com.example.services.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//TODO: consider what is better: getting userId from frontend directly, or by token
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user/{userId}/person")
public class PersonAddingController {
    private final PersonService personService;

    @PostMapping("/add")
    public ResponseEntity<Void> add(
            @PathVariable("userId") String userId,
            @RequestBody Person person) {

        personService.add(userId, person);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/{personId}/add-spouse")
    public ResponseEntity<Void> addSpouse(
            @PathVariable("userId") String userId,
            @PathVariable("personId") String personId,
            @RequestBody Person spouse) {

        try {
            personService.addSpouse(userId, personId, spouse);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/{personId}/add-parent")
    public ResponseEntity<Void> addParent(
            @PathVariable("userId") String userId,
            @PathVariable("personId") String personId,
            @RequestBody Person parent) {

        try {
            personService.addParent(userId, personId, parent);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
