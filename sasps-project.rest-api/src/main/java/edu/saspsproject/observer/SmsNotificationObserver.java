package edu.saspsproject.observer;

import edu.saspsproject.adapter.SmsProviderFactory;
import edu.saspsproject.model.Appointment;
import edu.saspsproject.model.User;
import edu.saspsproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/**
 * OBSERVER PATTERN - SMS Notification Observer
 * 
 * Observer concret care trimite notificări prin SMS.
 * Trimite SMS-uri scurte pentru evenimentele critice.
 * 
 * Prioritate medie (50) - SMS-urile sunt trimise după email.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SmsNotificationObserver implements AppointmentObserver {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM HH:mm");
    private static final int MAX_SMS_LENGTH = 160;
    
    private final UserRepository userRepository;
    private final SmsProviderFactory smsProviderFactory;
    
    @Override
    public void onAppointmentCreated(Appointment appointment) {
        log.info("[SMS-OBSERVER] Procesare eveniment CREATED pentru #{}", appointment.getId());
        
        User user = getUserSafely(appointment);
        if (user == null || user.getPhone() == null) {
            log.debug("Nu se trimite SMS - utilizator sau telefon lipsă");
            return;
        }
        
        String message = truncateMessage(String.format(
                "SASPS: Programare #%d confirmata pentru %s. Prezentati-va cu 10 min inainte.",
                appointment.getId(),
                appointment.getAppointmentTime().format(DATE_FORMATTER)
        ));
        
        smsProviderFactory.sendSms(user.getPhone(), message);
    }
    
    @Override
    public void onAppointmentConfirmed(Appointment appointment) {
        // Similar cu onAppointmentCreated
        log.debug("[SMS-OBSERVER] Eveniment CONFIRMED procesat similar cu CREATED");
    }
    
    @Override
    public void onAppointmentCancelled(Appointment appointment, String reason) {
        log.info("[SMS-OBSERVER] Procesare eveniment CANCELLED pentru #{}", appointment.getId());
        
        User user = getUserSafely(appointment);
        if (user == null || user.getPhone() == null) {
            return;
        }
        
        String message = truncateMessage(String.format(
                "SASPS: Programare #%d ANULATA. Motiv: %s. Reprogramati online.",
                appointment.getId(),
                reason != null ? reason : "nespecificat"
        ));
        
        smsProviderFactory.sendSms(user.getPhone(), message);
    }
    
    @Override
    public void onAppointmentCompleted(Appointment appointment) {
        // Nu trimitem SMS la completare
        log.debug("[SMS-OBSERVER] Eveniment COMPLETED - nu se trimite SMS");
    }
    
    @Override
    public void onAppointmentReminder(Appointment appointment) {
        log.info("[SMS-OBSERVER] Procesare eveniment REMINDER pentru #{}", appointment.getId());
        
        User user = getUserSafely(appointment);
        if (user == null || user.getPhone() == null) {
            return;
        }
        
        String message = truncateMessage(String.format(
                "SASPS REMINDER: Programare #%d maine la %s. Nu uitati actele!",
                appointment.getId(),
                appointment.getAppointmentTime().format(DATE_FORMATTER)
        ));
        
        smsProviderFactory.sendSms(user.getPhone(), message);
    }
    
    @Override
    public int getPriority() {
        return 50; // Prioritate medie - după email
    }
    
    @Override
    public String getObserverName() {
        return "SmsNotificationObserver";
    }
    
    private User getUserSafely(Appointment appointment) {
        if (appointment.getUserId() == null) return null;
        return userRepository.findById(appointment.getUserId()).orElse(null);
    }
    
    private String truncateMessage(String message) {
        if (message.length() <= MAX_SMS_LENGTH) {
            return message;
        }
        return message.substring(0, MAX_SMS_LENGTH - 3) + "...";
    }
}
