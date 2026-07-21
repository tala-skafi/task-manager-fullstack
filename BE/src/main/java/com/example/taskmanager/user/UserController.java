package com.example.taskmanager.user;

import com.example.taskmanager.common.Role;
import com.example.taskmanager.common.UserStatus;
import com.example.taskmanager.user.dto.CreateUserRequest;
import com.example.taskmanager.user.dto.UpdateUserRequest;
import com.example.taskmanager.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** User management; restricted to ADMIN by the security configuration. */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> list(@RequestParam(required = false) String search,
                                   @RequestParam(required = false) Role role,
                                   @RequestParam(required = false) UserStatus status) {
        return userService.search(search, role, status);
    }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable Long id) {
        return userService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return userService.create(request);
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication authentication) {
        Long currentUserId = userService.findByUsername(authentication.getName()).getId();
        userService.delete(id, currentUserId);
    }
}
