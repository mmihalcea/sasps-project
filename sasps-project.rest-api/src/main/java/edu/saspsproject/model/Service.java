package edu.saspsproject.model;

import lombok.Data;

@Data
public class Service {
    private Long id;
    private String name;
    private String institutionType;
    private Double estimatedDuration; // minutes
    private String requiredDocuments;
    private String description;
    private Double fee;
    private String category;
    private Boolean requiresAppointment;
    private String priorityHandling;
}