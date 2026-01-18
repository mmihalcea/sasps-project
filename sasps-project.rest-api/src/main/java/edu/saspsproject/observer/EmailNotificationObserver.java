package edu.saspsproject.observer;

import edu.saspsproject.adapter.EmailProvider;
import edu.saspsproject.adapter.EmailProviderFactory;
import edu.saspsproject.model.Appointment;
import edu.saspsproject.model.Institution;
import edu.saspsproject.model.User;
import edu.saspsproject.repository.InstitutionRepository;
import edu.saspsproject.repository.UserRepository;
import edu.saspsproject.template.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * OBSERVER PATTERN - Email Notification Observer
 * 
 * Observer concret care trimite notificări prin email.
 * Utilizează Adapter Pattern pentru selectarea provider-ului
 * și Template Method Pattern pentru generarea conținutului.
 * 
 * Combinație de pattern-uri:
 * - Observer: reacționează la evenimente
 * - Adapter: selectează provider-ul potrivit
 * - Template Method: generează email-ul
 * 
 * Prioritate ridicată (100) pentru a trimite email-uri înaintea altor notificări.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationObserver implements AppointmentObserver {
    
    private final UserRepository userRepository;
    private final InstitutionRepository institutionRepository;
    private final EmailProviderFactory emailProviderFactory;
    private final AppointmentConfirmationEmailTemplate confirmationTemplate;
    private final AppointmentReminderEmailTemplate reminderTemplate;
    private final AppointmentCancellationEmailTemplate cancellationTemplate;
    
    @Override
    public void onAppointmentCreated(Appointment appointment) {
        log.info("[EMAIL-OBSERVER] Procesare eveniment CREATED pentru #{}", appointment.getId());
        sendConfirmationEmail(appointment);
    }
    
    @Override
    public void onAppointmentConfirmed(Appointment appointment) {
        log.info("[EMAIL-OBSERVER] Procesare eveniment CONFIRMED pentru #{}", appointment.getId());
        sendConfirmationEmail(appointment);
    }
    
    @Override
    public void onAppointmentCancelled(Appointment appointment, String reason) {
        log.info("[EMAIL-OBSERVER] Procesare eveniment CANCELLED pentru #{}", appointment.getId());
        
        User user = getUserSafely(appointment);
        Institution institution = getInstitutionSafely(appointment);
        
        if (user == null || institution == null) {
            log.warn("Nu se poate trimite email de anulare - date lipsă");
            return;
        }
        
        EmailProvider provider = emailProviderFactory.getProvider(institution.getType());
        AppointmentCancellationData data = new AppointmentCancellationData(
                appointment, institution.getName(), reason);
        
        cancellationTemplate.sendEmail(user, data, provider);
    }
    
    @Override
    public void onAppointmentCompleted(Appointment appointment) {
        log.info("[EMAIL-OBSERVER] Procesare eveniment COMPLETED pentru #{}", appointment.getId());
        // Opțional: trimite email de feedback sau mulțumire
    }
    
    @Override
    public void onAppointmentReminder(Appointment appointment) {
        log.info("[EMAIL-OBSERVER] Procesare eveniment REMINDER pentru #{}", appointment.getId());
        
        User user = getUserSafely(appointment);
        Institution institution = getInstitutionSafely(appointment);
        
        if (user == null || institution == null) {
            log.warn("Nu se poate trimite reminder - date lipsă");
            return;
        }
        
        EmailProvider provider = emailProviderFactory.getProvider(institution.getType());
        AppointmentEmailData data = new AppointmentEmailData(appointment, institution.getName());
        
        reminderTemplate.sendEmail(user, data, provider);
    }
    
    @Override
    public int getPriority() {
        return 100; // Prioritate ridicată - email-urile sunt importante
    }
    
    @Override
    public String getObserverName() {
        return "EmailNotificationObserver";
    }
    
    private void sendConfirmationEmail(Appointment appointment) {
        User user = getUserSafely(appointment);
        Institution institution = getInstitutionSafely(appointment);
        
        if (user == null || institution == null) {
            log.warn("Nu se poate trimite email de confirmare - date lipsă");
            return;
        }
        
        EmailProvider provider = emailProviderFactory.getProvider(institution.getType());
        AppointmentEmailData data = new AppointmentEmailData(appointment, institution.getName());
        
        confirmationTemplate.sendEmail(user, data, provider);
    }
    
    private User getUserSafely(Appointment appointment) {
        if (appointment.getUserId() == null) return null;
        return userRepository.findById(appointment.getUserId()).orElse(null);
    }
    
    private Institution getInstitutionSafely(Appointment appointment) {
        if (appointment.getInstitutionId() == null) return null;
        return institutionRepository.findById(appointment.getInstitutionId()).orElse(null);
    }
}
