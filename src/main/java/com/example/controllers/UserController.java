package com.example.controllers;

import com.example.config.security.JwtAuthenticationFilter;
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

    @GetMapping
    public UserDTO getDetails(@RequestHeader(JwtAuthenticationFilter.AUTH_HEADER) String token) {
        return userService.getDetails(token);
    }

    @PostMapping("family-tree/add")
    public ResponseEntity<FamilyTree> createFamilyTree(
            @RequestBody FamilyTree familyTree,
            @RequestHeader(JwtAuthenticationFilter.AUTH_HEADER) String token) {

        return new ResponseEntity<>(userService.createFamilyTree(familyTree, token), HttpStatus.CREATED);
    }
}
