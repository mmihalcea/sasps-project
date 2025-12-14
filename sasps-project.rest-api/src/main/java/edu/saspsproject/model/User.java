package edu.saspsproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "users")
public class User {
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;

    @Setter
    @Column(unique = true)
    private String email;

    @Setter
    private String phone;

    @Setter
    private String address;

    @Setter
    private String city;

    @Setter
    private String county;

    @Setter
    @Column(name = "date_of_birth")
    private LocalDateTime dateOfBirth;

    @Setter
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Setter
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Setter
    @Column(name = "email_notifications_enabled")
    private Boolean emailNotificationsEnabled = true;

    @Setter
    @Column(name = "sms_notifications_enabled")
    private Boolean smsNotificationsEnabled = false;

    @Setter
    @Column(name = "reminder_hours_before")
    private Integer reminderHoursBefore = 24;

    @Setter
    @Column(name = "preferred_language")
    private String preferredLanguage = "ro";

    @Setter
    private Boolean active = true;

    @Setter
    @Column(name = "user_role")
    private String role = "USER"; // USER, ADMIN

    @Setter
    private String password; // Simplistic no encryption for baseline version

    public User() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}