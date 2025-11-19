package edu.saspsproject.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;

@Data
@Entity
@Table
public class PublicService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Setter
    @Enumerated(EnumType.STRING)
    private Institution.InstitutionType institutionType;
    @Column(nullable = false)
    private Double estimatedDuration; // minutes
    private String requiredDocuments;
    private String description;
    private Double fee;
    private String category;
    private Boolean requiresAppointment;
    private String priorityHandling;
}