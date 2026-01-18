package edu.saspsproject.template;

import edu.saspsproject.model.Appointment;

/**
 * RECORD pentru datele necesare email-urilor de anulare.
 * 
 * Extinde datele standard cu motivul anulării.
 * 
 * @param appointment obiectul Appointment anulat
 * @param institutionName numele instituției
 * @param reason motivul anulării (opțional)
 */
public record AppointmentCancellationData(
        Appointment appointment,
        String institutionName,
        String reason
) {
    /**
     * Constructor cu validare.
     */
    public AppointmentCancellationData {
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null");
        }
        if (institutionName == null || institutionName.isBlank()) {
            institutionName = "Instituție necunoscută";
        }
    }
    
    /**
     * Constructor fără motiv specificat.
     */
    public AppointmentCancellationData(Appointment appointment, String institutionName) {
        this(appointment, institutionName, null);
    }
}
