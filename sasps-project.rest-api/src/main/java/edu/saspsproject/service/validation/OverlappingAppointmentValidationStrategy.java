package edu.saspsproject.service.validation;

import edu.saspsproject.model.Appointment;
import edu.saspsproject.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OverlappingAppointmentValidationStrategy implements AppointmentValidationStrategy {

    private final AppointmentRepository appointmentRepository;

    @Override
    public void validate(Appointment appointment) {
        List<Appointment> overlappingAppointments = appointmentRepository.findByInstitutionIdAndAppointmentTime(
                appointment.getInstitutionId(),
                appointment.getAppointmentTime()
        );
        if (!overlappingAppointments.isEmpty()) {
            throw new IllegalStateException("Appointment overlaps with an existing appointment.");
        }
    }
}
