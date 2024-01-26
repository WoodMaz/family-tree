package com.example.dto.mappers;

import com.example.dto.UserDTO;
import com.example.models.User;

public class UserMapper {
    public static UserDTO asDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getRole());
    }
}
