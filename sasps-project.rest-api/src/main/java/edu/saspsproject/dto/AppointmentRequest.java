package edu.saspsproject.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentRequest {
    private Long institutionId;
    private LocalDateTime appointmentTime;
    private String customerName;
    private String customerEmail;
}