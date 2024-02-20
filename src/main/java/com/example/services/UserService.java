package com.example.services;

import com.example.config.security.JwtService;
import com.example.dto.UserDTO;
import com.example.dto.mappers.UserMapper;
import com.example.models.User;
import com.example.repositories.UserRepository;
import com.example.utils.ThrowingUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public UserDTO getDetails(String token) {
        return UserMapper.asDTO(getByToken(token));
    }

    public User getByToken(String token) {
        String username = jwtService.extractUsername(token);
        return userRepository.getByUsername(username)
                .orElseThrow(ThrowingUtils.userNotFound(username));
    }

    public void addFamilyTree(String familyTreeId, String token) {
        User user = getByToken(token);
        user.getFamilyTreeIds().add(familyTreeId);
        userRepository.save(user);
    }
}
