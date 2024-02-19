package com.example.services;

import com.example.config.security.JwtService;
import com.example.dto.UserDTO;
import com.example.dto.mappers.UserMapper;
import com.example.models.FamilyTree;
import com.example.models.User;
import com.example.repositories.FamilyTreeRepository;
import com.example.repositories.UserRepository;
import com.example.utils.ThrowingUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final FamilyTreeRepository familyTreeRepository;

    public UserDTO getDetails(String token) {
        return UserMapper.asDTO(getByToken(token));
    }

    public User getByToken(String token) {
        String username = jwtService.extractUsername(token);
        return userRepository.getByUsername(username)
                .orElseThrow(ThrowingUtil.userNotFound(username));
    }

    public FamilyTree createFamilyTree(FamilyTree familyTree, String token) {
        User user = getByToken(token);

        familyTreeRepository.save(familyTree);

        user.addFamilyTree(familyTree.getId());
        userRepository.save(user);

        return familyTree;
    }

    public void addFamilyTree(String familyTreeId, String token) {
        User user = getByToken(token);
        user.getFamilyTreeIds().add(familyTreeId);
        userRepository.save(user);
    }
}
