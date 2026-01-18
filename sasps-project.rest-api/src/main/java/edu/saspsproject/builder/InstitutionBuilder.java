package edu.saspsproject.builder;

import edu.saspsproject.model.Institution;
import edu.saspsproject.model.County;

import java.time.LocalTime;

/**
 * BUILDER PATTERN - Institution Builder
 * 
 * Permite construirea obiectelor Institution într-un mod fluent.
 * Util pentru teste și pentru inițializarea datelor.
 * 
 * Utilizare:
 * <pre>
 * Institution institution = InstitutionBuilder.builder()
 *     .withName("Primăria Sector 1")
 *     .ofType(InstitutionType.PRIMARIA)
 *     .locatedAt("Str. Primăverii, Nr. 1")
 *     .withPhone("021-123-4567")
 *     .openFrom(LocalTime.of(8, 0))
 *     .until(LocalTime.of(16, 0))
 *     .inCounty(county)
 *     .build();
 * </pre>
 */
public class InstitutionBuilder {
    
    private String name;
    private Institution.InstitutionType type;
    private String address;
    private String phone;
    private LocalTime openingTime = LocalTime.of(8, 0);
    private LocalTime closingTime = LocalTime.of(16, 0);
    private Integer maxAppointmentsPerDay = 50;
    private Double averageServiceTime = 30.0;
    private String specialRequirements;
    private Boolean requiresDocuments = true;
    private Institution.NotificationType notificationPreferences = Institution.NotificationType.EMAIL;
    private County county;
    
    /**
     * Creează un nou builder pentru Institution.
     */
    public InstitutionBuilder() {
    }
    
    /**
     * Metodă factory statică pentru a crea un builder.
     */
    public static InstitutionBuilder builder() {
        return new InstitutionBuilder();
    }
    
    /**
     * Setează numele instituției.
     */
    public InstitutionBuilder withName(String name) {
        this.name = name;
        return this;
    }
    
    /**
     * Setează tipul instituției.
     */
    public InstitutionBuilder ofType(Institution.InstitutionType type) {
        this.type = type;
        return this;
    }
    
    /**
     * Setează adresa instituției.
     */
    public InstitutionBuilder locatedAt(String address) {
        this.address = address;
        return this;
    }
    
    /**
     * Setează numărul de telefon.
     */
    public InstitutionBuilder withPhone(String phone) {
        this.phone = phone;
        return this;
    }
    
    /**
     * Setează ora de deschidere.
     */
    public InstitutionBuilder openFrom(LocalTime openingTime) {
        this.openingTime = openingTime;
        return this;
    }
    
    /**
     * Setează ora de închidere.
     */
    public InstitutionBuilder until(LocalTime closingTime) {
        this.closingTime = closingTime;
        return this;
    }
    
    /**
     * Setează numărul maxim de programări pe zi.
     */
    public InstitutionBuilder withMaxAppointmentsPerDay(Integer max) {
        this.maxAppointmentsPerDay = max;
        return this;
    }
    
    /**
     * Setează durata medie a serviciului.
     */
    public InstitutionBuilder withAverageServiceTime(Double time) {
        this.averageServiceTime = time;
        return this;
    }
    
    /**
     * Setează cerințele speciale.
     */
    public InstitutionBuilder withSpecialRequirements(String requirements) {
        this.specialRequirements = requirements;
        return this;
    }
    
    /**
     * Setează dacă sunt necesare documente.
     */
    public InstitutionBuilder requiresDocuments(Boolean requires) {
        this.requiresDocuments = requires;
        return this;
    }
    
    /**
     * Setează preferințele de notificare.
     */
    public InstitutionBuilder withNotificationPreferences(Institution.NotificationType pref) {
        this.notificationPreferences = pref;
        return this;
    }
    
    /**
     * Setează județul.
     */
    public InstitutionBuilder inCounty(County county) {
        this.county = county;
        return this;
    }
    
    /**
     * Construiește obiectul Institution.
     */
    public Institution build() {
        validate();
        
        Institution institution = new Institution();
        institution.setName(name);
        institution.setType(type);
        institution.setAddress(address);
        institution.setPhone(phone);
        institution.setOpeningTime(openingTime);
        institution.setClosingTime(closingTime);
        institution.setMaxAppointmentsPerDay(maxAppointmentsPerDay);
        institution.setAverageServiceTime(averageServiceTime);
        institution.setSpecialRequirements(specialRequirements);
        institution.setRequiresDocuments(requiresDocuments);
        institution.setNotificationPreferences(notificationPreferences);
        institution.setCounty(county);
        
        return institution;
    }
    
    private void validate() {
        if (name == null || name.isBlank()) {
            throw new IllegalStateException("Numele instituției este obligatoriu");
        }
        if (type == null) {
            throw new IllegalStateException("Tipul instituției este obligatoriu");
        }
    }
}
