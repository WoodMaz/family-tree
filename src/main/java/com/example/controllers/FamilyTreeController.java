package com.example.controllers;

import com.example.config.security.JwtAuthenticationFilter;
import com.example.dto.FamilyTreeMergeRequest;
import com.example.models.FamilyTree;
import com.example.models.Person;
import com.example.services.FamilyTreeService;
import com.example.services.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/family-tree")
public class FamilyTreeController {
    private final PersonService personService;
    private final FamilyTreeService familyTreeService;

    @GetMapping("/{familyTreeId}")
    public ResponseEntity<List<Person>> getFamilyTree(
            @PathVariable String familyTreeId,
            @RequestHeader(JwtAuthenticationFilter.AUTH_HEADER) String token) {

        try {
            return ResponseEntity.ok(personService.getAllByFamilyTreeId(familyTreeId, token));
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<FamilyTree> createFamilyTree(
            @RequestBody FamilyTree familyTree,
            @RequestHeader(JwtAuthenticationFilter.AUTH_HEADER) String token) {

        return new ResponseEntity<>(familyTreeService.createFamilyTree(familyTree, token), HttpStatus.CREATED);
    }

    @GetMapping("/{familyTreeId}/export/gedcom")
    public ResponseEntity<String> exportToGedcom(
            @PathVariable String familyTreeId,
            @RequestHeader(JwtAuthenticationFilter.AUTH_HEADER) String token) {

        try {
            return ResponseEntity.ok(personService.exportFamilyTree(familyTreeId, token));
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    //TODO: endpoint to get a list of people that exist in several familyTrees
    @PostMapping("/merge")
    public ResponseEntity<FamilyTree> mergeFamilyTrees(
            @RequestBody FamilyTreeMergeRequest request,
            @RequestHeader(JwtAuthenticationFilter.AUTH_HEADER) String token) {
        try {
            return new ResponseEntity<>(
                    familyTreeService.mergeFamilyTrees(request.getFamilyTreeIds(), request.getMergedTree(), token),
                    HttpStatus.CREATED);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/{familyTreeId}/person/add")
    public ResponseEntity<Person> add(
            @PathVariable("familyTreeId") String familyTreeId,
            @RequestBody Person person,
            @RequestHeader(JwtAuthenticationFilter.AUTH_HEADER) String token) {

        try {
            return new ResponseEntity<>(personService.add(familyTreeId, person, token), HttpStatus.CREATED);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/{familyTreeId}/person/{personId}/add-spouse")
    public ResponseEntity<Person> addSpouse(
            @PathVariable("familyTreeId") String familyTreeId,
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

    @PostMapping("/{familyTreeId}/person/{personId}/add-parent")
    public ResponseEntity<Person> addParent(
            @PathVariable("familyTreeId") String familyTreeId,
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

    @DeleteMapping("/delete/{familyTreeId}")
    public ResponseEntity<Void> deleteFamilyTree(
            @PathVariable("familyTreeId") String familyTreeId,
            @RequestHeader(JwtAuthenticationFilter.AUTH_HEADER) String token) {
        try {
            familyTreeService.delete(familyTreeId, token);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
