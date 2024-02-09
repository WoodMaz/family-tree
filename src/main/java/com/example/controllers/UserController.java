package com.example.controllers;

import com.example.models.FamilyTree;
import com.example.models.Person;
import com.example.dto.UserDTO;
import com.example.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    private UserService userService;

    @GetMapping("/id/{id}")
    public UserDTO getUserById(@PathVariable String id) {
        return userService.getById(id);
    }

    @GetMapping("/username/{username}")
    public UserDTO getUserByName(@PathVariable String username) {
        return userService.getByUsername(username);
    }

    @GetMapping("family-tree/{familyTreeId}")
    public List<Person> getFamilyTree(@PathVariable String familyTreeId) {
        return userService.getAllByFamilyTreeId(familyTreeId);
    }

    @GetMapping("{username}/family-tree/{id}/export/gedcom")
    public ResponseEntity<String> exportToGedcom(@PathVariable String username, @PathVariable String familyTreeId) {
        String data = userService.exportFamilyTree(username, familyTreeId);
        return ResponseEntity.ok(data);
    }

    @PostMapping("{userId}/family-tree")
    public ResponseEntity<FamilyTree> createFamilyTree(@PathVariable String userId, @RequestBody FamilyTree familyTree) {
        return new ResponseEntity<>(userService.createFamilyTree(userId, familyTree), HttpStatus.CREATED);
    }
}
