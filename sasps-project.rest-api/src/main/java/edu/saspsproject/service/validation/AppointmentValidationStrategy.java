package edu.saspsproject.service.validation;

import edu.saspsproject.model.Appointment;

public interface AppointmentValidationStrategy {
    void validate(Appointment appointment);
}
