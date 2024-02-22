package com.example.controllers;

import com.example.config.security.JwtAuthenticationFilter;
import com.example.dto.FamilyTreeMergeRequest;
import com.example.models.FamilyTree;
import com.example.models.Person;
import com.example.services.FamilyTreeService;
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
    private final FamilyTreeService familyTreeService;

    @PostMapping
    public ResponseEntity<FamilyTree> createFamilyTree(
            @RequestBody FamilyTree familyTree,
            @RequestHeader(JwtAuthenticationFilter.AUTH_HEADER) String token) {

        return new ResponseEntity<>(familyTreeService.createFamilyTree(familyTree, token), HttpStatus.CREATED);
    }

    @GetMapping("/{familyTreeId}")
    public ResponseEntity<List<Person>> getFamilyTree(
            @PathVariable String familyTreeId,
            @RequestHeader(JwtAuthenticationFilter.AUTH_HEADER) String token) {

        try {
            return ResponseEntity.ok(familyTreeService.getAllFamilyTreeMembers(familyTreeId, token));
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/{familyTreeId}")
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

    @GetMapping("/{familyTreeId}/export/gedcom")
    public ResponseEntity<String> exportToGedcom(
            @PathVariable String familyTreeId,
            @RequestHeader(JwtAuthenticationFilter.AUTH_HEADER) String token) {

        try {
            return ResponseEntity.ok(familyTreeService.exportFamilyTree(familyTreeId, token));
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
}
