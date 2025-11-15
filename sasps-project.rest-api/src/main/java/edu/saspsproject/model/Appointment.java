package edu.saspsproject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "appointments")
public class Appointment {
    public enum Status {
        PENDING, CONFIRMED, COMPLETED, CANCELLED
    }

    public enum ServiceType {
        ELIBERARE_CI, CERTIFICAT_NASTERE, DECLARATIE_FISCALA
    }

    public enum PriorityLevel {
        LOW, MEDIUM, HIGH, URGENT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "institution_id", nullable = false)
    private Long institutionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", insertable = false, updatable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Institution institution;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User user;

    @NotBlank
    @Size(max = 120)
    private String title;

    @Size(max = 500)
    private String notes;

    @NotNull
    private LocalDateTime appointmentTime;

    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    @Enumerated(EnumType.STRING)
    private PriorityLevel priorityLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @NotNull
    private Double estimatedDuration;

    @NotNull
    private String documentRequired;

    @NotNull
    private Boolean reminderSent;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;
}