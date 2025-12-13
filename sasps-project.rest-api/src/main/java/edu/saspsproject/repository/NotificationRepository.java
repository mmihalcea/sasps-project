package edu.saspsproject.repository;

import edu.saspsproject.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByAppointmentId(Long appointmentId);
    List<Notification> findByInstitutionId(Long institutionId);
    List<Notification> findByUserId(Long userId);
    List<Notification> findByStatus(Notification.NotificationStatus status);
    List<Notification> findByType(Notification.NotificationType type);
    List<Notification> findByMethod(Notification.NotificationMethod method);
    List<Notification> findBySentAtBetween(LocalDateTime start, LocalDateTime end);
    List<Notification> findByUserIdAndStatus(Long userId, Notification.NotificationStatus status);
    
    // For admin dashboard
    List<Notification> findAllByOrderBySentAtDesc();
    List<Notification> findByStatusOrderBySentAtDesc(Notification.NotificationStatus status);
}
