package com.example.services;

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
    private final UserRepository userRepository;
    private final FamilyTreeRepository familyTreeRepository;

    public UserDTO getByUsername(String username) {
        return UserMapper.asDTO(
                userRepository
                        .getByUsername(username)
                        .orElseThrow(ThrowingUtil.userNotFound(username))
        );
    }

    public FamilyTree createFamilyTree(String userId, FamilyTree familyTree) {
        User user = userRepository.getById(userId);
        familyTreeRepository.save(familyTree);

        user.addFamilyTree(familyTree.getId());
        userRepository.save(user);

        return familyTree;
    }
}
