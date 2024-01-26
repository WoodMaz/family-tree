package com.example.controllers;

import com.example.models.Person;
import com.example.services.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//TODO: endpoints to create relations with existing people (marriage, parent-child)
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/person")
public class PersonController {
    private final PersonService personService;

    @GetMapping("/show/{id}")
    public ResponseEntity<Person> getPerson(@PathVariable String personId) {
        try {
            return new ResponseEntity<>(personService.getById(personId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("wife/{wifeId}/husband/{husbandId}")
    public ResponseEntity<Void> createMarriage(
            @PathVariable("wifeId") String wifeId,
            @PathVariable("husbandId") String husbandId) {
        return null;
    }
}