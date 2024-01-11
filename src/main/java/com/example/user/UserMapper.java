package com.example.user;

public class UserMapper {
    public static UserDTO asDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getFamilyTree());
    }
}
