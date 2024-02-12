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

    @GetMapping("/{personId}/get-children")
    public ResponseEntity<List<Person>> getChildren(
            @PathVariable String personId,
            @RequestHeader(JwtAuthenticationFilter.AUTH_HEADER) String token)
            throws AuthenticationException {

        return ResponseEntity.ok(personService.getChildren(personId, token));
    }

    @GetMapping("/{personId}/get-spouses")
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

        personService.updatePerson(person, token);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{personId}/delete")
    public ResponseEntity<Void> deletePerson(
            @PathVariable String personId,
            @RequestHeader(JwtAuthenticationFilter.AUTH_HEADER) String token)
            throws AuthenticationException {

        personService.deleteById(personId, token);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/spouse1/{spouse1Id}/spouse2/{spouse2Id}")
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

    @PutMapping("/undo/spouse1/{spouse1Id}/spouse2/{spouse2Id}")
    public ResponseEntity<Void> undoBondSpouses(
            @PathVariable("spouse1Id") String spouse1Id,
            @PathVariable("spouse2Id") String spouse2Id,
            @RequestHeader(JwtAuthenticationFilter.AUTH_HEADER) String token)
            throws AuthenticationException {

        personService.undoBondSpouses(spouse1Id, spouse2Id, token);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/undo/child/{childId}/parent/{parentId}")
    public ResponseEntity<Void> undoBondChildWithParent(
            @PathVariable("childId") String childId,
            @PathVariable("parentId") String parentId,
            @RequestHeader(JwtAuthenticationFilter.AUTH_HEADER) String token)
            throws AuthenticationException {

        personService.undoBondChildWithParent(childId, parentId, token);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}