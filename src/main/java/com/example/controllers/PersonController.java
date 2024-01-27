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
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("spouse1/{spouse1Id}/spouse2/{spouse2Id}")
    public ResponseEntity<Void> bondSpouses(
            @PathVariable("spouse1Id") String spouse1Id,
            @PathVariable("spouse2Id") String spouse2Id) {

        personService.bondSpouses(spouse1Id, spouse2Id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("child/{childId}/parent/{parentId}")
    public ResponseEntity<Void> bondChildWithParent(
            @PathVariable("childId") String childId,
            @PathVariable("parentId") String parentId) {

        personService.bondChildWithParent(childId, parentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}