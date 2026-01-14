package edu.saspsproject.service.validation;

import edu.saspsproject.model.Appointment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AppointmentValidator {

    private final List<AppointmentValidationStrategy> validationStrategies;

    public void validate(Appointment appointment) {
        for (AppointmentValidationStrategy strategy : validationStrategies) {
            strategy.validate(appointment);
        }
    }
}
