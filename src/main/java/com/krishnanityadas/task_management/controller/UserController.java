package com.krishnanityadas.task_management.controller;


import com.krishnanityadas.task_management.service.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.krishnanityadas.task_management.dto.authentication.LoginRequest;
import com.krishnanityadas.task_management.dto.authentication.RegisterRequest;
import com.krishnanityadas.task_management.dto.authentication.SignUpResponse;
import com.krishnanityadas.task_management.dto.authentication.ValidateResponse;
import com.krishnanityadas.task_management.enums.Role;
import com.krishnanityadas.task_management.model.User;
import com.krishnanityadas.task_management.service.JwtService;
import com.krishnanityadas.task_management.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@CrossOrigin(origins = "https://task-management-60k8d7l35-lavish-singlas-projects.vercel.app", allowCredentials = "true")

public class UserController {
    private final CurrentUserService currentUserService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    UserController(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @PostMapping("/register")
    public ResponseEntity<SignUpResponse> register(
            @Valid @RequestBody RegisterRequest userRequest, HttpServletResponse response) {
        User user = new User();
        user.setUserName(userRequest.userName());
        user.setEmail(userRequest.email());
        user.setPassword(userRequest.password());
        userRequest.userRole().ifPresent(user::setUserRole);
        if (user.getUserRole() == null) {
            user.setUserRole(Role.USER);
        }
        user.setEmail(user.getEmail().toLowerCase());
        user.setPassword(encoder.encode(user.getPassword()));
        userService.addUser(user);

        String token = jwtService.generateToken(user.getUserName());
        ResponseCookie cookie = ResponseCookie.from("token", token).httpOnly(true).secure(false)
                .path("/").maxAge(4 * 24 * 60 * 60).sameSite("Lax").build();

        response.addHeader("Set-Cookie", cookie.toString());

        return new ResponseEntity<>(SignUpResponse.from(user), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest user,
            HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.userName(), user.password()));

            String token = jwtService.generateToken(user.userName());
            ResponseCookie cookie = ResponseCookie.from("token", token).httpOnly(true).secure(false)
                    .path("/").maxAge(4 * 24 * 60 * 60).sameSite("Lax").build();

            response.addHeader("Set-Cookie", cookie.toString());

            return new ResponseEntity<>("Login successfull", HttpStatus.OK);
        } catch (BadCredentialsException ex) {
            logger.warn("Login failed for user '{}': invalid credentials", user.userName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (AuthenticationException ex) {
            logger.error("Authentication error for user '{}': {}", user.userName(), ex.getMessage(),
                    ex);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception ex) {
            logger.error("Unexpected login error for user '{}': {}", user.userName(),
                    ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/auth/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "").httpOnly(true).secure(false)
                .path("/").maxAge(0).sameSite("Lax").build();

        response.addHeader("Set-Cookie", cookie.toString());

        return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);
    }

    @GetMapping("/validate/user")
    public ResponseEntity<ValidateResponse> isValidUser(Authentication authentication) {
        Boolean isUserAuthenticated = currentUserService.isUserAuthenticated(authentication);
        return new ResponseEntity<>(ValidateResponse.from(isUserAuthenticated), HttpStatus.OK);
    }

    @GetMapping("/users/all")
    public ResponseEntity<List<SignUpResponse>> getAllUsers() {
        List<User> allUsers = userService.getAllUsers();
        return new ResponseEntity<>(SignUpResponse.fromList(allUsers), HttpStatus.OK);
    }

}
