package edu.saspsproject.observer;

import edu.saspsproject.model.Appointment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * OBSERVER PATTERN - Appointment Event Publisher (Subject)
 * 
 * Gestionează lista de observatori și publică evenimente despre programări.
 * Funcționează ca "Subject" în terminologia Observer Pattern.
 * 
 * Caracteristici:
 * - Descoperire automată a observatorilor prin Spring DI
 * - Sortare după prioritate
 * - Tratare erori pentru a preveni întreruperea lanțului de notificări
 * - Async support pentru operații costisitoare
 * 
 * Utilizare:
 * <pre>
 * // În AppointmentService
 * appointmentEventPublisher.publishCreated(savedAppointment);
 * </pre>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentEventPublisher {
    
    private final List<AppointmentObserver> observers;
    
    /**
     * Publică eveniment de creare programare către toți observatorii.
     * 
     * @param appointment programarea creată
     */
    public void publishCreated(Appointment appointment) {
        log.info("📢 Publicare eveniment: APPOINTMENT_CREATED pentru #{}", appointment.getId());
        notifyObservers(observer -> observer.onAppointmentCreated(appointment));
    }
    
    /**
     * Publică eveniment de confirmare programare.
     * 
     * @param appointment programarea confirmată
     */
    public void publishConfirmed(Appointment appointment) {
        log.info("📢 Publicare eveniment: APPOINTMENT_CONFIRMED pentru #{}", appointment.getId());
        notifyObservers(observer -> observer.onAppointmentConfirmed(appointment));
    }
    
    /**
     * Publică eveniment de anulare programare.
     * 
     * @param appointment programarea anulată
     * @param reason motivul anulării
     */
    public void publishCancelled(Appointment appointment, String reason) {
        log.info("📢 Publicare eveniment: APPOINTMENT_CANCELLED pentru #{}", appointment.getId());
        notifyObservers(observer -> observer.onAppointmentCancelled(appointment, reason));
    }
    
    /**
     * Publică eveniment de completare programare.
     * 
     * @param appointment programarea completată
     */
    public void publishCompleted(Appointment appointment) {
        log.info("📢 Publicare eveniment: APPOINTMENT_COMPLETED pentru #{}", appointment.getId());
        notifyObservers(observer -> observer.onAppointmentCompleted(appointment));
    }
    
    /**
     * Publică eveniment de reminder.
     * 
     * @param appointment programarea pentru reminder
     */
    public void publishReminder(Appointment appointment) {
        log.info("📢 Publicare eveniment: APPOINTMENT_REMINDER pentru #{}", appointment.getId());
        notifyObservers(observer -> observer.onAppointmentReminder(appointment));
    }
    
    /**
     * Notifică toți observatorii în ordinea priorității.
     * Erorile sunt logged dar nu opresc procesarea celorlalți observatori.
     */
    private void notifyObservers(java.util.function.Consumer<AppointmentObserver> action) {
        observers.stream()
                .sorted(Comparator.comparingInt(AppointmentObserver::getPriority).reversed())
                .forEach(observer -> {
                    try {
                        log.debug("Notificare observer: {}", observer.getObserverName());
                        action.accept(observer);
                    } catch (Exception e) {
                        log.error("Eroare la notificarea observer-ului {}: {}", 
                                observer.getObserverName(), e.getMessage(), e);
                    }
                });
    }
    
    /**
     * Returnează numărul de observatori înregistrați.
     * Util pentru debugging și monitorizare.
     */
    public int getObserverCount() {
        return observers.size();
    }
}
