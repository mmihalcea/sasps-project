package edu.saspsproject.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentRequest {
    private Long institutionId;
    private String institutionType;
    private LocalDateTime appointmentTime;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String serviceType;
    private String priorityLevel;
    private String notes;
    private String documentRequired;
}