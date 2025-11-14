package edu.saspsproject.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Appointment {
    private Long id;
    private Long institutionId;
    private String institutionType; // PRIMARIA, ANAF, ANPC, POLITIA_LOCALA
    private LocalDateTime appointmentTime;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String serviceType; // ELIBERARE_CI, CERTIFICAT_NASTERE, DECLARATIE_FISCALA, etc.
    private String status; // PENDING, CONFIRMED, COMPLETED, CANCELLED
    private String priorityLevel; // LOW, NORMAL, HIGH, URGENT
    private String notes;
    private Double estimatedDuration; // in minutes
    private String documentRequired;
    private Boolean reminderSent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}