package edu.saspsproject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Entity
@Table(name = "appointments")
public class Appointment {

    public enum Status {
        SCHEDULED, CANCELED, DONE
    }

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User user;

    @Setter
    @NotBlank
    @Size(max = 120)
    private String title;

    @Setter
    @Size(max = 500)
    private String notes;

    @Setter
    @NotNull
    private Date date;

    @Setter
    @NotNull
    private LocalDateTime startTime;

    @Setter
    @NotNull
    private LocalDateTime endTime;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.SCHEDULED;
}