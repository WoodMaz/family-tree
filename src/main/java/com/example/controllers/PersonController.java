package com.example.controllers;

import com.example.config.security.JwtAuthenticationFilter;
import com.example.models.Person;
import com.example.services.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/person")
public class PersonController {
    private final PersonService personService;

    @GetMapping("/{personId}")
    public ResponseEntity<Person> getPerson(
            @PathVariable String personId,
            @RequestHeader(JwtAuthenticationFilter.AUTH_HEADER) String token) {

        try {
            return new ResponseEntity<>(personService.getById(personId, token), HttpStatus.OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{personId}/children")
    public ResponseEntity<List<Person>> getChildren(
            @PathVariable String personId,
            @RequestHeader(JwtAuthenticationFilter.AUTH_HEADER) String token)
            throws AuthenticationException {

        return ResponseEntity.ok(personService.getChildren(personId, token));
    }

    @GetMapping("/{personId}/spouses")
    public ResponseEntity<List<Person>> getSpouses(
            @PathVariable String personId,
            @RequestHeader(JwtAuthenticationFilter.AUTH_HEADER) String token)
            throws AuthenticationException {
        return ResponseEntity.ok(personService.getSpouses(personId, token));
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updatePerson(
            @RequestBody Person person,
            @RequestHeader(JwtAuthenticationFilter.AUTH_HEADER) String token)
            throws AuthenticationException {

        personService.update(person, token);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{personId}")
    public ResponseEntity<Void> deletePerson(
            @PathVariable String personId,
            @RequestHeader(JwtAuthenticationFilter.AUTH_HEADER) String token)
            throws AuthenticationException {

        personService.deleteById(personId, token);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/marriage/{spouse1Id}/{spouse2Id}")
    public ResponseEntity<Void> bondSpouses(
            @PathVariable("spouse1Id") String spouse1Id,
            @PathVariable("spouse2Id") String spouse2Id,
            @RequestHeader(JwtAuthenticationFilter.AUTH_HEADER) String token)
            throws AuthenticationException {

        personService.bondSpouses(spouse1Id, spouse2Id, token);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/child/{childId}/parent/{parentId}")
    public ResponseEntity<Void> bondChildWithParent(
            @PathVariable("childId") String childId,
            @PathVariable("parentId") String parentId,
            @RequestHeader(JwtAuthenticationFilter.AUTH_HEADER) String token)
            throws AuthenticationException {

        personService.bondChildWithParent(childId, parentId, token);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/marriage/{spouse1Id}/{spouse2Id}")
    public ResponseEntity<Void> debondSpouses(
            @PathVariable("spouse1Id") String spouse1Id,
            @PathVariable("spouse2Id") String spouse2Id,
            @RequestHeader(JwtAuthenticationFilter.AUTH_HEADER) String token)
            throws AuthenticationException {

        personService.debondSpouses(spouse1Id, spouse2Id, token);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/child/{childId}/parent/{parentId}")
    public ResponseEntity<Void> debondChildFromParent(
            @PathVariable("childId") String childId,
            @PathVariable("parentId") String parentId,
            @RequestHeader(JwtAuthenticationFilter.AUTH_HEADER) String token)
            throws AuthenticationException {

        personService.debondChildFromParent(childId, parentId, token);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Person> add(
            @RequestParam("familyTreeId") String familyTreeId,
            @RequestBody Person person,
            @RequestHeader(JwtAuthenticationFilter.AUTH_HEADER) String token) {

        try {
            return new ResponseEntity<>(personService.add(familyTreeId, person, token), HttpStatus.CREATED);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("{personId}/spouse")
    public ResponseEntity<Person> addSpouse(
            @RequestParam("familyTreeId") String familyTreeId,
            @PathVariable("personId") String personId,
            @RequestBody Person spouse,
            @RequestHeader(JwtAuthenticationFilter.AUTH_HEADER) String token) {

        try {
            return new ResponseEntity<>(personService.addSpouse(familyTreeId, personId, spouse, token), HttpStatus.CREATED);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("{personId}/parent")
    public ResponseEntity<Person> addParent(
            @RequestParam("familyTreeId") String familyTreeId,
            @PathVariable("personId") String personId,
            @RequestBody Person parent,
            @RequestHeader(JwtAuthenticationFilter.AUTH_HEADER) String token) {

        try {
            return new ResponseEntity<>(personService.addParent(familyTreeId, personId, parent, token), HttpStatus.CREATED);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}