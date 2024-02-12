package com.example.controllers;

import com.example.models.FamilyTree;
import com.example.dto.UserDTO;
import com.example.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{username}")
    public UserDTO getUserByName(@PathVariable String username) {
        return userService.getByUsername(username);
    }

    @PostMapping("{userId}/family-tree")
    public ResponseEntity<FamilyTree> createFamilyTree(
            @PathVariable String userId,
            @RequestBody FamilyTree familyTree) {

        return new ResponseEntity<>(userService.createFamilyTree(userId, familyTree), HttpStatus.CREATED);
    }
}
