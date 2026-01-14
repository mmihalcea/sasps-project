package edu.saspsproject.service.validation;

import edu.saspsproject.model.Appointment;
import edu.saspsproject.model.Institution;
import edu.saspsproject.repository.InstitutionRepository;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;

@Component
public class BusinessHoursValidationStrategy implements AppointmentValidationStrategy {

    public static final String APPOINTMENT_HOUR_MESSAGE = "Appointments can only be scheduled between {0} and {1}.";
    private final InstitutionRepository institutionRepository;

    public BusinessHoursValidationStrategy(InstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
    }

    @Override
    public void validate(Appointment appointment) {
        DayOfWeek dayOfWeek = appointment.getAppointmentTime().getDayOfWeek();
        LocalTime time = appointment.getAppointmentTime().toLocalTime();

        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            throw new IllegalStateException("Appointments cannot be scheduled on weekends.");
        }

        Optional<Institution> institution = institutionRepository.findById(appointment.getInstitutionId());

        institution.ifPresent(inst -> {
            if (time.isBefore(inst.getOpeningTime()) || time.isAfter(inst.getClosingTime())) {
                throw new IllegalStateException(MessageFormat.format(APPOINTMENT_HOUR_MESSAGE, inst.getOpeningTime(), inst.getClosingTime()));
            }
        });

    }
}
