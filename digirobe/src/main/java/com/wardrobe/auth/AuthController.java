package com.wardrobe.auth;

import com.wardrobe.security.JwtTokenProvider;
import com.wardrobe.user.User;
import com.wardrobe.user.UserRepository;
import com.wardrobe.user.UserService; 
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtTokenProvider jwtTokenProvider, UserRepository userRepository, PasswordEncoder passwordEncoder, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody AuthRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest request) throws Exception {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        final String jwt = jwtTokenProvider.generateToken(userDetails);
        return ResponseEntity.ok(Map.of("token", jwt));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> payload) {
        String userEmail = payload.get("email");
        userService.handleForgotPassword(userEmail);
        
        // For security, we send a generic success message whether the email exists or not.
        return ResponseEntity.ok("If an account with that email exists, a password reset link has been sent.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token, @RequestBody Map<String, String> payload) {
        System.out.println("BACKEND RECEIVED TOKEN FOR RESET: " + token);
        try {
            String newPassword = payload.get("password");
            if (newPassword == null || newPassword.isEmpty()) {
                return ResponseEntity.badRequest().body("New password cannot be empty.");
            }

            userService.handleResetPassword(token, newPassword);

            return ResponseEntity.ok("Your password has been successfully reset.");
        } catch (IllegalArgumentException e) {
            // This will catch "Invalid token" or "Token expired" errors from our service.
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}