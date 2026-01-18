package edu.saspsproject.observer;

import edu.saspsproject.model.Appointment;
import edu.saspsproject.model.Notification;
import edu.saspsproject.model.User;
import edu.saspsproject.repository.NotificationRepository;
import edu.saspsproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * OBSERVER PATTERN - Database Notification Observer
 * 
 * Observer concret care salvează notificările în baza de date.
 * Permite utilizatorilor să vadă istoricul notificărilor în UI.
 * 
 * Prioritate scăzută (10) - rulează după email și SMS.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseNotificationObserver implements AppointmentObserver {
    
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    
    @Override
    public void onAppointmentCreated(Appointment appointment) {
        log.info("[DB-OBSERVER] Salvare notificare CREATED pentru #{}", appointment.getId());
        
        saveNotification(
                appointment.getUserId(),
                "Programarea dumneavoastră #" + appointment.getId() + " a fost creată cu succes!",
                Notification.NotificationType.CONFIRMATION
        );
    }
    
    @Override
    public void onAppointmentConfirmed(Appointment appointment) {
        log.info("[DB-OBSERVER] Salvare notificare CONFIRMED pentru #{}", appointment.getId());
        
        saveNotification(
                appointment.getUserId(),
                "Programarea #" + appointment.getId() + " a fost confirmată.",
                Notification.NotificationType.CONFIRMATION
        );
    }
    
    @Override
    public void onAppointmentCancelled(Appointment appointment, String reason) {
        log.info("[DB-OBSERVER] Salvare notificare CANCELLED pentru #{}", appointment.getId());
        
        String message = "Programarea #" + appointment.getId() + " a fost anulată.";
        if (reason != null && !reason.isBlank()) {
            message += " Motiv: " + reason;
        }
        
        saveNotification(
                appointment.getUserId(),
                message,
                Notification.NotificationType.CANCELLATION
        );
    }
    
    @Override
    public void onAppointmentCompleted(Appointment appointment) {
        log.info("[DB-OBSERVER] Salvare notificare COMPLETED pentru #{}", appointment.getId());
        
        saveNotification(
                appointment.getUserId(),
                "Programarea #" + appointment.getId() + " a fost finalizată. Mulțumim!",
                Notification.NotificationType.ANNOUNCEMENT
        );
    }
    
    @Override
    public void onAppointmentReminder(Appointment appointment) {
        log.info("[DB-OBSERVER] Salvare notificare REMINDER pentru #{}", appointment.getId());
        
        saveNotification(
                appointment.getUserId(),
                "Reminder: Aveți programarea #" + appointment.getId() + " mâine!",
                Notification.NotificationType.REMINDER
        );
    }
    
    @Override
    public int getPriority() {
        return 10; // Prioritate scăzută - rulează ultimul
    }
    
    @Override
    public String getObserverName() {
        return "DatabaseNotificationObserver";
    }
    
    private void saveNotification(Long userId, String message, Notification.NotificationType type) {
        if (userId == null) {
            log.warn("Nu se poate salva notificarea - userId null");
            return;
        }
        
        User user = userRepository.findById(userId).orElse(null);
        
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setType(type);
        notification.setMethod(Notification.NotificationMethod.EMAIL);
        notification.setStatus(Notification.NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.now());
        
        if (user != null) {
            notification.setRecipientEmail(user.getEmail());
            notification.setRecipientPhone(user.getPhone());
        }
        
        notificationRepository.save(notification);
        log.debug("Notificare salvată în DB pentru utilizator #{}", userId);
    }
}
