package com.example.controllers;

import com.example.config.security.JwtAuthenticationFilter;
import com.example.dto.UserDTO;
import com.example.services.UserService;
import lombok.RequiredArgsConstructor;
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

}
