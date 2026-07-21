package com.example.taskmanager.profile;

import com.example.taskmanager.common.exception.BadRequestException;
import com.example.taskmanager.profile.dto.ChangePasswordRequest;
import com.example.taskmanager.profile.dto.UpdateProfileRequest;
import com.example.taskmanager.user.User;
import com.example.taskmanager.user.UserRepository;
import com.example.taskmanager.user.UserService;
import com.example.taskmanager.user.dto.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileService(UserService userService,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public UserResponse getProfile(String username) {
        return UserResponse.from(userService.findByUsername(username));
    }

    @Transactional
    public UserResponse updateProfile(String username, UpdateProfileRequest request) {
        User user = userService.findByUsername(username);

        if (userRepository.existsByEmailAndIdNot(request.email(), user.getId())) {
            throw new BadRequestException("Email is already in use");
        }

        user.setName(request.name());
        user.setEmail(request.email());
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userService.findByUsername(username);

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }
}
