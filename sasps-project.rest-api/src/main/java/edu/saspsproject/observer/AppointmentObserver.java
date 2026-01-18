package edu.saspsproject.observer;

import edu.saspsproject.model.Appointment;

/**
 * OBSERVER PATTERN - Appointment Event Observer Interface
 * 
 * Definește contractul pentru observatorii care ascultă evenimente legate de programări.
 * Permite decuplarea sistemului de notificări de logica de business.
 * 
 * Beneficii:
 * - Decuplare între producători și consumatori de evenimente
 * - Ușurință în adăugarea de noi tipuri de notificări
 * - Respectă Open/Closed Principle
 * - Facilitează testarea componentelor individual
 * 
 * Implementări tipice:
 * - EmailNotificationObserver - trimite email-uri
 * - SmsNotificationObserver - trimite SMS-uri
 * - DatabaseNotificationObserver - salvează în DB
 * - PushNotificationObserver - notificări push
 * 
 * @see AppointmentEventPublisher
 * @see EmailNotificationObserver
 */
public interface AppointmentObserver {
    
    /**
     * Apelat când o programare nouă este creată.
     * 
     * @param appointment programarea creată
     */
    void onAppointmentCreated(Appointment appointment);
    
    /**
     * Apelat când o programare este confirmată.
     * 
     * @param appointment programarea confirmată
     */
    void onAppointmentConfirmed(Appointment appointment);
    
    /**
     * Apelat când o programare este anulată.
     * 
     * @param appointment programarea anulată
     * @param reason motivul anulării (poate fi null)
     */
    void onAppointmentCancelled(Appointment appointment, String reason);
    
    /**
     * Apelat când o programare este completată.
     * 
     * @param appointment programarea completată
     */
    void onAppointmentCompleted(Appointment appointment);
    
    /**
     * Apelat pentru reminder-uri de programări.
     * 
     * @param appointment programarea pentru care se trimite reminder
     */
    void onAppointmentReminder(Appointment appointment);
    
    /**
     * Returnează prioritatea observatorului.
     * Observatorii cu prioritate mai mare sunt notificați primii.
     * 
     * @return prioritatea (default 0)
     */
    default int getPriority() {
        return 0;
    }
    
    /**
     * Returnează numele observatorului pentru logging.
     * 
     * @return numele observatorului
     */
    String getObserverName();
}
