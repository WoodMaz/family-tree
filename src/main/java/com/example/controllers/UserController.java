package com.example.controllers;

import com.example.models.Person;
import com.example.dto.UserDTO;
import com.example.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    private UserService userService;

    @GetMapping("/id/{id}")
    public UserDTO getUserDetails(@PathVariable String id) {
        return userService.getById(id);
    }

    @GetMapping("/username/{username}")
    public UserDTO getUserByName(@PathVariable String username) {
        return userService.getByUsername(username);
    }

    @GetMapping("/{userId}/get-family-tree")
    public Set<Person> getFamilyTree(@PathVariable String userId) {
        return userService.getFamilyTree(userId);
    }

    @PatchMapping("/{userId}/add-person")
    public void addPerson(@PathVariable String userId, @RequestBody Person person) {
        userService.addPerson(userId, person);
    }
}
