package com.example.controllers;

import com.example.models.Person;
import com.example.services.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/family-tree/{familyTreeId}/person")
public class PersonAddingController {
    private final PersonService personService;

    @PostMapping("/add")
    public ResponseEntity<Void> add(
            @PathVariable("familyTreeId") String familyTreeId,
            @RequestBody Person person) {

        personService.add(familyTreeId, person);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/{personId}/add-spouse")
    public ResponseEntity<Void> addSpouse(
            @PathVariable("familyTreeId") String familyTreeId,
            @PathVariable("personId") String personId,
            @RequestBody Person spouse) {

        try {
            personService.addSpouse(familyTreeId, personId, spouse);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/{personId}/add-parent")
    public ResponseEntity<Void> addParent(
            @PathVariable("familyTreeId") String familyTreeId,
            @PathVariable("personId") String personId,
            @RequestBody Person parent) {

        try {
            personService.addParent(familyTreeId, personId, parent);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
