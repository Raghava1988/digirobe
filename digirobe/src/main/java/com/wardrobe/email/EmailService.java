package com.wardrobe.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service // Marks this class as a Spring-managed Service component.
public class EmailService {

    private final JavaMailSender emailSender;

    // Spring will automatically find the JavaMailSender bean we configured
    // in application.properties and inject it here.
    @Autowired
    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    /**
     * A simple method to send a plain text email.
     * @param to The email address of the recipient.
     * @param subject The subject line of the email.
     * @param text The plain text body of the email.
     */
    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@digirobe.com"); // You can set a 'from' address if you like
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            emailSender.send(message);
        } catch (Exception e) {
            // In a real application, you might have more robust logging or error handling.
            // For now, we'll just print the error if something goes wrong.
            System.err.println("Error sending email: " + e.getMessage());
        }
    }
} 