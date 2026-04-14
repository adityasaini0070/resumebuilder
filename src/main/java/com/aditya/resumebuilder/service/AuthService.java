package com.aditya.resumebuilder.service;

import com.aditya.resumebuilder.document.User;
import com.aditya.resumebuilder.dto.AuthResponse;
import com.aditya.resumebuilder.dto.LoginRequest;
import com.aditya.resumebuilder.dto.RegisterRequest;
import com.aditya.resumebuilder.exception.ResourceExistsException;
import com.aditya.resumebuilder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.base.url:http://localhost:8083}")
    private String appBaseUrl;

    public AuthResponse register(RegisterRequest request) {
        log.info("Inside AuthService: register() {}", request);
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceExistsException("User already exists with this email");
        }
        User newUser = toDocument(request);

        userRepository.save(newUser);

        sendVerificationEmail(newUser);

        return toResponse(newUser);
    }

    public void verifyEmail(String token) {
        log.info("Inside AuthService: verifyEmail() {}", token);
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        if (user.getVerificationExpires()!=null && user.getVerificationExpires().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification token has expired");
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationExpires(null);

        userRepository.save(user);
    }

    private void sendVerificationEmail(User newUser) {
        log.info("inside AuthService - sendVerificationEmail(): {}", newUser);
        try{
            String link = appBaseUrl+"/api/auth/verifyemail?token="+newUser.getVerificationToken();
            String html = "<div style='font-family:sans-serif'>" +
                    "<h2>Verify your email</h2>" +
                    "<p>hi "+ newUser.getName() + ", please confirm your email to activate your account" +
                    "<p><a href='" + link
                    + "' style='display:inline-block;padding:10px 16px;background:#6366f1;color:#fff;border-radius:6px;text-decoration:none'>verify email</a></p>" +
                    "<p> or copy this link: " + link + "</p>" +
                    "<p>this link expires in 24 hours.</p>" +
                    "</div";
            emailService.sendHtmlEmail(newUser.getEmail(), "verify your email", html);
        } catch(Exception e){
            log.error("exception occurred at sendVerificationEmail(): {}", e.getMessage());
            throw new RuntimeException("failed to send verification email: " + e.getMessage());
        }
    }

    private AuthResponse toResponse(User newUser){
        return AuthResponse.builder()
                .id(newUser.getId())
                .name(newUser.getName())
                .email(newUser.getEmail())
                .profileImageUrl(newUser.getProfileImageUrl())
                .emailVerified(newUser.getEmailVerified())
                .subscriptionPlan(newUser.getSubscriptionPlan())
                .createdAt(newUser.getCreatedAt())
                .updatedAt(newUser.getUpdatedAt())
                .build();
    }
    private User toDocument(RegisterRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .profileImageUrl(request.getProfileImageUrl())
                .subscriptionPlan("Basic")
                .emailVerified(false)
                .verificationToken(UUID.randomUUID().toString())
                .verificationExpires(LocalDateTime.now().plusHours(24))
                .build();
    }

    public AuthResponse login(LoginRequest request){
        log.info("Inside AuthController: login() {}", request);
        User existingUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("invalid email or password"));
        if(!passwordEncoder.matches(request.getPassword(), existingUser.getPassword())) {
            throw new BadCredentialsException("invalid password");
        }
        if(!existingUser.getEmailVerified()) throw new RuntimeException("please verify your email before logging in");
        String token = "jwtToken";
        AuthResponse response = toResponse(existingUser);
        response.setToken(token);
        return response;
    }
}
