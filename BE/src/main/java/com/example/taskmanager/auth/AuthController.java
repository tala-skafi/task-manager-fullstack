package com.example.taskmanager.auth;

import com.example.taskmanager.activity.ActivityActions;
import com.example.taskmanager.activity.ActivityService;
import com.example.taskmanager.security.JwtService;
import com.example.taskmanager.user.User;
import com.example.taskmanager.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ActivityService activityService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserRepository userRepository,
                          ActivityService activityService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.activityService = activityService;
    }

    @PostMapping("/login")
    @Transactional
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        // Throws BadCredentials/DisabledException on failure (handled globally).
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        User user = userRepository.findByUsername(request.username()).orElseThrow();
        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());

        activityService.record(user, ActivityActions.LOGIN, "User logged in");

        return new LoginResponse(token, user.getId(), user.getName(), user.getUsername(), user.getRole().name());
    }

    /** With stateless JWT the client just drops the token; this records the logout event. */
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void logout(Authentication authentication) {
        if (authentication != null) {
            activityService.record(ActivityActions.LOGOUT, "User logged out");
        }
    }
}
