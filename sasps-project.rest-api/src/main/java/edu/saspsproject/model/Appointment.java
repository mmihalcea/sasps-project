package edu.saspsproject.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Appointment {
    private Long id;
    private Long institutionId;
    private LocalDateTime appointmentTime;
    private String customerName;
    private String customerEmail;
}