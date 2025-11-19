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
        PRIMARIA, ANAF, ANPC, POLITIA_LOCALA, DRPCIV, SPCLEP
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
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "institutions_services",
            joinColumns = @JoinColumn(name = "institution_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id"))
    private List<PublicService> availableServices;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "county_id")
    @Setter
    private County county;


    public Institution() {}

}