package edu.saspsproject.template;

import edu.saspsproject.model.Appointment;

/**
 * RECORD pentru datele necesare generării email-urilor despre programări.
 * 
 * Utilizat de template-urile de email pentru a păstra toate datele
 * necesare într-un obiect imutabil și type-safe.
 * 
 * @param appointment obiectul Appointment cu detaliile programării
 * @param institutionName numele instituției (denormalizat pentru performanță)
 */
public record AppointmentEmailData(
        Appointment appointment,
        String institutionName
) {
    /**
     * Constructor cu validare.
     */
    public AppointmentEmailData {
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null");
        }
        if (institutionName == null || institutionName.isBlank()) {
            institutionName = "Instituție necunoscută";
        }
    }
}
