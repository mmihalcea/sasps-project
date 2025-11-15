package edu.saspsproject.repository;

import edu.saspsproject.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByAppointmentId(Long appointmentId);
    List<Notification> findByInstitutionId(Long institutionId);
}
