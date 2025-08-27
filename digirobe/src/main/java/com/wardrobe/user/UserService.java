package com.wardrobe.user;

import com.wardrobe.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service // Marks this class as a Spring-managed Service.
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // Spring will inject the components we need into our service.
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    /**
     * Creates a password reset token for a user and sends them an email with the link.
     * @param userEmail The email of the user who forgot their password.
     */
    public void handleForgotPassword(String userEmail) {
        // 1. Find the user by their email address.
        Optional<User> userOptional = userRepository.findByEmail(userEmail);

        // 2. For security, we don't reveal if an email is registered or not.
        // We'll proceed silently whether the user is found or not, but only send an email if they exist.
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // 3. Generate a unique, random token.
            String token = UUID.randomUUID().toString();
            System.out.println("BACKEND CREATED TOKEN: " + token); 

            // 4. Set the token and an expiration date (1 hour from now) on the user object.
            user.setResetPasswordToken(token);
            user.setResetPasswordTokenExpiryDate(LocalDateTime.now().plusHours(1));

            // 5. Save the updated user object to the database.
            userRepository.save(user);

            // 6. Create the password reset link for the email.
            // This URL should point to your frontend's reset password page.
            String resetUrl = "http://localhost:3000/reset-password?token=" + token;

            // 7. Use our EmailService to send the email.
            String subject = "Reset Your Digirobe Password";
            String text = "You have requested to reset your password.\n\n"
                        + "Please click the link below to set a new one. This link will expire in one hour.\n"
                        + resetUrl + "\n\n"
                        + "If you did not request this, please ignore this email.";
            
            emailService.sendSimpleMessage(user.getEmail(), subject, text);
        }
        // If userOptional is empty, the method simply finishes, leaking no information.
    }


    /**
     * Resets a user's password if the provided token is valid.
     * @param token The password reset token.
     * @param newPassword The new plain-text password.
     */
    public void handleResetPassword(String token, String newPassword) {
        // 1. Find the user by the reset token.
        Optional<User> userOptional = userRepository.findByResetPasswordToken(token);

        if (userOptional.isEmpty()) {
            // If no user is found with this token, it's invalid.
            throw new IllegalArgumentException("Invalid password reset token.");
        }

        User user = userOptional.get();

        // 2. Check if the token has expired.
        if (user.getResetPasswordTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Password reset token has expired.");
        }

        // 3. Set the new password. THIS FIXES THE SECOND ISSUE.
        // We must hash the new password before saving it, just like during registration.
        user.setPassword(passwordEncoder.encode(newPassword));

        // 4. Invalidate the token. THIS FIXES THE FIRST ISSUE.
        // We set the token fields to null so it cannot be used again.
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiryDate(null);

        // 5. Save the user with the new password and cleared token.
        userRepository.save(user);
    }

}