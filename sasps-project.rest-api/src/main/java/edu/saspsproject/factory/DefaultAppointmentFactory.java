package edu.saspsproject.factory;

import edu.saspsproject.dto.request.AppointmentRequest;
import edu.saspsproject.model.Appointment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Locale;

@Component
public class DefaultAppointmentFactory implements AppointmentFactory {

    @Override
    public Appointment create(AppointmentRequest request, Long userId) {
        LocalDateTime now = LocalDateTime.now();

        Appointment appointment = new Appointment();
        appointment.setInstitutionId(request.getInstitutionId());
        appointment.setUserId(userId);
        appointment.setInstitutionType(request.getInstitutionType());

        String title = "Programare " + request.getServiceType() + " la instituția " + request.getInstitutionId();
        appointment.setTitle(title);

        appointment.setNotes(request.getNotes());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setServiceType(parseServiceType(request.getServiceType()));
        appointment.setPriorityLevel(parsePriorityLevel(request.getPriorityLevel()));
        appointment.setDocumentRequired(request.getDocumentRequired());
        appointment.setReminderSent(false);
        appointment.setCreatedAt(now);
        appointment.setUpdatedAt(now);

        return appointment;
    }

    private Appointment.ServiceType parseServiceType(String value) {
        try {
            return Appointment.ServiceType.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid service type: " + value);
        }
    }

    private Appointment.PriorityLevel parsePriorityLevel(String value) {
        if (value == null || value.isBlank()) {
            return Appointment.PriorityLevel.MEDIUM;
        }
        try {
            return Appointment.PriorityLevel.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid priority level: " + value);
        }
    }
}