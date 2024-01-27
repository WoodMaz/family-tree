package com.example.controllers;

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

    @GetMapping("/show/{personId}")
    public ResponseEntity<Person> getPerson(@PathVariable String personId) {
        try {
            return new ResponseEntity<>(personService.getById(personId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{personId}/get-children")
    public ResponseEntity<List<Person>> getChildren(@PathVariable String personId) {
        return ResponseEntity.ok(personService.getChildren(personId));
    }

    @GetMapping("/{personId}/get-spouses")
    public ResponseEntity<List<Person>> getSpouses(@PathVariable String personId) {
        return ResponseEntity.ok(personService.getSpouses(personId));
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updatePerson(@RequestBody Person person) {
        personService.updatePerson(person);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{personId}/delete")
    public ResponseEntity<Void> deletePerson(@PathVariable String personId) {
        personService.deleteById(personId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/spouse1/{spouse1Id}/spouse2/{spouse2Id}")
    public ResponseEntity<Void> bondSpouses(
            @PathVariable("spouse1Id") String spouse1Id,
            @PathVariable("spouse2Id") String spouse2Id) {

        personService.bondSpouses(spouse1Id, spouse2Id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/child/{childId}/parent/{parentId}")
    public ResponseEntity<Void> bondChildWithParent(
            @PathVariable("childId") String childId,
            @PathVariable("parentId") String parentId) {

        personService.bondChildWithParent(childId, parentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/undo/spouse1/{spouse1Id}/spouse2/{spouse2Id}")
    public ResponseEntity<Void> undoBondSpouses(
            @PathVariable("spouse1Id") String spouse1Id,
            @PathVariable("spouse2Id") String spouse2Id) {

        personService.undoBondSpouses(spouse1Id, spouse2Id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/undo/child/{childId}/parent/{parentId}")
    public ResponseEntity<Void> undoBondChildWithParent(
            @PathVariable("childId") String childId,
            @PathVariable("parentId") String parentId) {

        personService.undoBondChildWithParent(childId, parentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}