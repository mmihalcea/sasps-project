package edu.saspsproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Entity
@Table(name = "institutions")
public class Institution {
    public enum InstitutionType {
        PRIMARIA, ANAF, ANPC, POLITIA_LOCALA
    }

    public enum NotificationType {
        EMAIL, SMS, PHONE
    }

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;

    @Setter
    @Enumerated(EnumType.STRING)
    private InstitutionType type;

    @Setter
    private String address;

    @Setter
    private String phone;

    @Setter
    private LocalTime openingTime;

    @Setter
    private LocalTime closingTime;

    @Setter
    private List<String> availableServices;

    @Setter
    private Integer maxAppointmentsPerDay;

    @Setter
    private Double averageServiceTime;

    @Setter
    private String specialRequirements;

    @Setter
    private Boolean requiresDocuments;

    @Setter
    @Enumerated(EnumType.STRING)
    private NotificationType notificationPreferences;


    public Institution() {}

}