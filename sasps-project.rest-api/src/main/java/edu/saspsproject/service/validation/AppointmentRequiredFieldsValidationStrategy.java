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
        if (appointment.getUser() == null || appointment.getUser().getName().isBlank()) {
            throw new IllegalArgumentException("Customer is required");
        }
        if (appointment.getUser() == null || !appointment.getUser().getEmail().contains("@")) {
            throw new IllegalArgumentException("Valid email is required");
        }
        if (appointment.getServiceType() == null) {
            throw new IllegalArgumentException("Service type is required");
        }
    }
}
