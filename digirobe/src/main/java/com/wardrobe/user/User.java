package com.wardrobe.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_user") // "user" is a reserved keyword in some SQL dialects
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "reset_token_expiry_date")
    private LocalDateTime resetPasswordTokenExpiryDate;

    // Constructors, Getters, Setters
    public User() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

        // --- ADDED GETTERS AND SETTERS FOR PASSWORD RESET ---
    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public LocalDateTime getResetPasswordTokenExpiryDate() {
        return resetPasswordTokenExpiryDate;
    }

    public void setResetPasswordTokenExpiryDate(LocalDateTime resetPasswordTokenExpiryDate) {
        this.resetPasswordTokenExpiryDate = resetPasswordTokenExpiryDate;
    }
    // ----------------------------------------------------

}