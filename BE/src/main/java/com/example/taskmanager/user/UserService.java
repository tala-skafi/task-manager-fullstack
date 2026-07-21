package com.example.taskmanager.user;

import com.example.taskmanager.activity.ActivityActions;
import com.example.taskmanager.activity.ActivityService;
import com.example.taskmanager.common.Role;
import com.example.taskmanager.common.UserStatus;
import com.example.taskmanager.common.exception.BadRequestException;
import com.example.taskmanager.common.exception.ResourceNotFoundException;
import com.example.taskmanager.user.dto.CreateUserRequest;
import com.example.taskmanager.user.dto.UpdateUserRequest;
import com.example.taskmanager.user.dto.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ActivityService activityService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       ActivityService activityService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.activityService = activityService;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> search(String search, Role role, UserStatus status) {
        return userRepository.search(emptyToNull(search), role, status).stream()
                .map(UserResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse get(Long id) {
        return UserResponse.from(findById(id));
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BadRequestException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email is already in use");
        }

        User user = new User();
        user.setName(request.name());
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setStatus(request.status());

        User saved = userRepository.save(user);
        activityService.record(ActivityActions.USER_CREATED, "Created user " + saved.getUsername());
        return UserResponse.from(saved);
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = findById(id);

        if (userRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new BadRequestException("Email is already in use");
        }

        user.setName(request.name());
        user.setEmail(request.email());
        user.setRole(request.role());
        user.setStatus(request.status());

        if (StringUtils.hasText(request.password())) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        User saved = userRepository.save(user);
        activityService.record(ActivityActions.USER_UPDATED, "Updated user " + saved.getUsername());
        return UserResponse.from(saved);
    }

    @Transactional
    public void delete(Long id, Long currentUserId) {
        User user = findById(id);
        if (user.getId().equals(currentUserId)) {
            throw new BadRequestException("You cannot delete your own account");
        }
        // Foreign-key rules unassign the user's tasks and remove their comments.
        userRepository.delete(user);
        activityService.record(ActivityActions.USER_DELETED, "Deleted user " + user.getUsername());
    }

    /** Shared lookup used by other services. */
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    private String emptyToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
