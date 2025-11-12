package edu.saspsproject.repository;

import edu.saspsproject.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    boolean existsByStartTimeLessThanEqualAndEndTimeGreaterThanEqual(LocalDateTime end, LocalDateTime start);
}