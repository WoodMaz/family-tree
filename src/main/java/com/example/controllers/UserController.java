package com.example.controllers;

import com.example.models.Person;
import com.example.dto.UserDTO;
import com.example.services.UserService;
import lombok.AllArgsConstructor;
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

    @GetMapping("/{userId}/get-family-tree")
    public List<Person> getFamilyTree(@PathVariable String userId) {
        return userService.getFamilyTree(userId);
    }

    @GetMapping("{id}/export/gedcom")
    public ResponseEntity<String> exportToGedcom(@PathVariable String id) {
        String data = userService.exportFamilyTree(id);
        return ResponseEntity.ok(data);
    }
}
