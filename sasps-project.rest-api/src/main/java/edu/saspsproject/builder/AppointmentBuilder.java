package edu.saspsproject.builder;

import edu.saspsproject.model.Appointment;

import java.time.LocalDateTime;

/**
 * BUILDER PATTERN - Appointment Builder
 * 
 * Permite construirea obiectelor Appointment într-un mod fluent și validat.
 * Rezolvă problema constructorilor cu mulți parametri (telescoping constructor anti-pattern).
 * 
 * Beneficii:
 * - Cod mai lizibil și mai ușor de înțeles
 * - Validare la momentul construcției
 * - Posibilitatea de a crea obiecte imutabile
 * - Self-documenting code - fiecare metodă are un nume descriptiv
 * - Previne crearea obiectelor într-o stare invalidă
 * 
 * Utilizare:
 * <pre>
 * Appointment appointment = new AppointmentBuilder()
 *     .forUser(userId)
 *     .atInstitution(institutionId)
 *     .withService(ServiceType.ELIBERARE_CI)
 *     .scheduledAt(LocalDateTime.now().plusDays(1))
 *     .withPriority(PriorityLevel.MEDIUM)
 *     .withNotes("Notă importantă")
 *     .requiringDocuments("Carte de identitate")
 *     .build();
 * </pre>
 * 
 * @see Appointment
 */
public class AppointmentBuilder {
    
    private Long userId;
    private Long institutionId;
    private String institutionType;
    private Appointment.ServiceType serviceType;
    private LocalDateTime appointmentTime;
    private Appointment.PriorityLevel priorityLevel = Appointment.PriorityLevel.MEDIUM;
    private Appointment.Status status = Appointment.Status.PENDING;
    private String title;
    private String notes;
    private String documentRequired;
    private Double estimatedDuration;
    private Boolean reminderSent = false;
    
    /**
     * Creează un nou builder pentru Appointment.
     */
    public AppointmentBuilder() {
        // Constructor gol - toate valorile au default-uri
    }
    
    /**
     * Setează utilizatorul pentru programare.
     * 
     * @param userId ID-ul utilizatorului
     * @return acest builder pentru method chaining
     */
    public AppointmentBuilder forUser(Long userId) {
        this.userId = userId;
        return this;
    }
    
    /**
     * Setează instituția pentru programare.
     * 
     * @param institutionId ID-ul instituției
     * @return acest builder
     */
    public AppointmentBuilder atInstitution(Long institutionId) {
        this.institutionId = institutionId;
        return this;
    }
    
    /**
     * Setează tipul instituției (string).
     * 
     * @param institutionType tipul instituției
     * @return acest builder
     */
    public AppointmentBuilder withInstitutionType(String institutionType) {
        this.institutionType = institutionType;
        return this;
    }
    
    /**
     * Setează tipul serviciului solicitat.
     * 
     * @param serviceType tipul serviciului
     * @return acest builder
     */
    public AppointmentBuilder withService(Appointment.ServiceType serviceType) {
        this.serviceType = serviceType;
        return this;
    }
    
    /**
     * Setează data și ora programării.
     * 
     * @param appointmentTime data și ora
     * @return acest builder
     */
    public AppointmentBuilder scheduledAt(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
        return this;
    }
    
    /**
     * Setează nivelul de prioritate.
     * 
     * @param priorityLevel prioritatea
     * @return acest builder
     */
    public AppointmentBuilder withPriority(Appointment.PriorityLevel priorityLevel) {
        this.priorityLevel = priorityLevel;
        return this;
    }
    
    /**
     * Setează statusul inițial.
     * 
     * @param status statusul
     * @return acest builder
     */
    public AppointmentBuilder withStatus(Appointment.Status status) {
        this.status = status;
        return this;
    }
    
    /**
     * Setează titlul programării.
     * 
     * @param title titlul
     * @return acest builder
     */
    public AppointmentBuilder withTitle(String title) {
        this.title = title;
        return this;
    }
    
    /**
     * Setează notele adiționale.
     * 
     * @param notes notele
     * @return acest builder
     */
    public AppointmentBuilder withNotes(String notes) {
        this.notes = notes;
        return this;
    }
    
    /**
     * Setează documentele necesare.
     * 
     * @param documentRequired lista de documente
     * @return acest builder
     */
    public AppointmentBuilder requiringDocuments(String documentRequired) {
        this.documentRequired = documentRequired;
        return this;
    }
    
    /**
     * Setează durata estimată în minute.
     * 
     * @param estimatedDuration durata în minute
     * @return acest builder
     */
    public AppointmentBuilder withEstimatedDuration(Double estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
        return this;
    }
    
    /**
     * Marchează că reminder-ul a fost trimis.
     * 
     * @param reminderSent true dacă a fost trimis
     * @return acest builder
     */
    public AppointmentBuilder withReminderSent(Boolean reminderSent) {
        this.reminderSent = reminderSent;
        return this;
    }
    
    /**
     * Construiește obiectul Appointment cu validare.
     * 
     * @return obiectul Appointment construit
     * @throws IllegalStateException dacă câmpurile obligatorii lipsesc
     */
    public Appointment build() {
        validate();
        
        LocalDateTime now = LocalDateTime.now();
        
        Appointment appointment = new Appointment();
        appointment.setUserId(userId);
        appointment.setInstitutionId(institutionId);
        appointment.setInstitutionType(institutionType);
        appointment.setServiceType(serviceType);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setPriorityLevel(priorityLevel);
        appointment.setStatus(status);
        appointment.setNotes(notes);
        appointment.setDocumentRequired(documentRequired);
        appointment.setEstimatedDuration(estimatedDuration != null ? estimatedDuration : 30.0);
        appointment.setReminderSent(reminderSent);
        appointment.setCreatedAt(now);
        appointment.setUpdatedAt(now);
        
        // Generare titlu automat dacă nu a fost specificat
        if (title == null || title.isBlank()) {
            appointment.setTitle(generateTitle());
        } else {
            appointment.setTitle(title);
        }
        
        return appointment;
    }
    
    /**
     * Validează că toate câmpurile obligatorii sunt setate.
     */
    private void validate() {
        StringBuilder errors = new StringBuilder();
        
        if (userId == null) {
            errors.append("userId este obligatoriu. ");
        }
        if (institutionId == null) {
            errors.append("institutionId este obligatoriu. ");
        }
        if (appointmentTime == null) {
            errors.append("appointmentTime este obligatoriu. ");
        }
        if (serviceType == null) {
            errors.append("serviceType este obligatoriu. ");
        }
        
        if (!errors.isEmpty()) {
            throw new IllegalStateException("Erori de validare AppointmentBuilder: " + errors);
        }
    }
    
    /**
     * Generează un titlu descriptiv automat.
     */
    private String generateTitle() {
        String serviceDesc = serviceType != null ? serviceType.name() : "Serviciu";
        return "Programare " + serviceDesc + " la instituția " + institutionId;
    }
    
    /**
     * Metodă factory statică pentru a crea un builder.
     * 
     * @return un nou AppointmentBuilder
     */
    public static AppointmentBuilder builder() {
        return new AppointmentBuilder();
    }
}
