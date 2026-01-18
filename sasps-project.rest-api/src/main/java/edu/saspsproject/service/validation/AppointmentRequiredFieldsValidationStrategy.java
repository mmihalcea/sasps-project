package edu.saspsproject.service.validation;

import edu.saspsproject.model.Appointment;
import org.springframework.stereotype.Component;

@Component
public class AppointmentRequiredFieldsValidationStrategy  implements AppointmentValidationStrategy{
    @Override
    public void validate(Appointment appointment) {
        if (appointment.getInstitutionId() == null) {
            throw new IllegalArgumentException("Institution ID is required");
        }
        if (appointment.getAppointmentTime() == null) {
            throw new IllegalArgumentException("Appointment time is required");
        }
        // User is created/found before validation, so we only need to check userId
        if (appointment.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (appointment.getServiceType() == null) {
            throw new IllegalArgumentException("Service type is required");
        }
    }
}
