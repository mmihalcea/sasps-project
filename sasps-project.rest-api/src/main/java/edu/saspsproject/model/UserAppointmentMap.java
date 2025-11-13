package edu.saspsproject.model;

import jakarta.persistence.*;
import lombok.Setter;

@Entity
@Table(name = "user_appointment_map")
public class UserAppointmentMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    public UserAppointmentMap() {}

    public UserAppointmentMap(User user, Appointment appointment) {
        this.user = user;
        this.appointment = appointment;
    }

    public Long getId() { return id; }

    public User getUser() { return user; }

    public Appointment getAppointment() { return appointment; }
}