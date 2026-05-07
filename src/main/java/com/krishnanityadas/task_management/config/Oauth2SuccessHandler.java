package com.krishnanityadas.task_management.config;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.krishnanityadas.task_management.enums.Role;
import com.krishnanityadas.task_management.model.User;
import com.krishnanityadas.task_management.repo.UserRepo;
import com.krishnanityadas.task_management.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {
    private static final int MAX_USERNAME_LENGTH = 50;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepo userRepo;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        System.out.println("Oauth called");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        OAuth2AuthenticationToken oAuth2AuthenticationToken =
                (OAuth2AuthenticationToken) authentication;
        String provider = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
        
        String userName = "";


        if ("github".equals(provider)) {
            userName = oAuth2User.getAttribute("login");
            findOrCreateUserForGithub(userName);
        }
        
        if ("google".equals(provider)) {
            
            String email = oAuth2User.getAttribute("email");

            if (email == null || email.isBlank()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email not found");
                return;
            }

            User user = findOrCreateUserForGoogle(email);
            userName = user.getUserName();
        }


        String token = jwtService.generateToken(userName);

        ResponseCookie cookie = ResponseCookie.from("token", token).httpOnly(true).secure(false) // true
                                                                                                 // in
                                                                                                 // production
                .path("/").maxAge(4 * 24 * 60 * 60).sameSite("Lax").build();

        response.addHeader("Set-Cookie", cookie.toString());

        response.sendRedirect("https://task-management-60k8d7l35-lavish-singlas-projects.vercel.app/workspace");
    }

    private User findOrCreateUserForGoogle(String email) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        User existingUser = userRepo.findByEmail(normalizedEmail);
        if (existingUser != null) {
            return existingUser;
        }

        User user = new User();
        user.setEmail(normalizedEmail);
        user.setUserName(generateUniqueUserName(normalizedEmail));
        user.setPassword(new BCryptPasswordEncoder(12).encode(UUID.randomUUID().toString()));
        user.setUserRole(Role.USER);

        return userRepo.save(user);
    }

    private User findOrCreateUserForGithub(String userName) {
        User existingUser = userRepo.findByUserName(userName);
        if (existingUser != null) {
            return existingUser;
        }

        User user = new User();
        user.setEmail(String.format("github.%s@gmail.com", userName));
        user.setUserName(userName);
        user.setPassword(new BCryptPasswordEncoder(12).encode(UUID.randomUUID().toString()));
        user.setUserRole(Role.USER);

        return userRepo.save(user);
    }



    private String generateUniqueUserName(String email) {
        String localPart = email.substring(0, email.indexOf('@'));
        String baseUserName = localPart.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (baseUserName.isBlank()) {
            baseUserName = "google_user";
        }

        baseUserName = trimToMaxLength(baseUserName, MAX_USERNAME_LENGTH);
        String candidate = baseUserName;
        int suffix = 1;

        while (userRepo.existsByUserName(candidate)) {
            String suffixText = "_" + suffix;
            candidate = trimToMaxLength(baseUserName, MAX_USERNAME_LENGTH - suffixText.length())
                    + suffixText;
            suffix++;
        }

        return candidate;
    }

    private String trimToMaxLength(String value, int maxLength) {
        if (value.length() <= maxLength) {
            return value;
        }

        return value.substring(0, maxLength);
    }
}
