package edu.saspsproject.repository;

import edu.saspsproject.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("select a from Appointment a join fetch a.institution i where i.id = :institutionId")
    List<Appointment> findByInstitutionIdWithInstitution(@Param("institutionId") Long institutionId);

    @Query("select a from Appointment a join fetch a.institution where a.id = :id")
    Optional<Appointment> findByIdWithInstitution(@Param("id") Long id);

    List<Appointment> findByInstitutionId(Long institutionId);

    List<Appointment> findByInstitutionIdAndAppointmentTime(Long institutionId, LocalDateTime appointmentTime);

    List<Appointment> findByUserId(Long userId);
}