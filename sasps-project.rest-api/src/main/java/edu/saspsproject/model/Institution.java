package edu.saspsproject.model;

import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class Institution {
    private Long id;
    private String name;
    private String type; // PRIMARIA, ANAF, ANPC, POLITIA_LOCALA
    private String address;
    private String phone;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private List<String> availableServices;
    private Integer maxAppointmentsPerDay;
    private Double averageServiceTime;
    private String specialRequirements;
    private Boolean requiresDocuments;
    private String notificationPreferences; // EMAIL, SMS, BOTH
}