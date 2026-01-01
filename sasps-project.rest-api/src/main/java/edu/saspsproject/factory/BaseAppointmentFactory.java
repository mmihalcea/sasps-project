package edu.saspsproject.factory;

import edu.saspsproject.dto.request.AppointmentRequest;
import edu.saspsproject.model.Appointment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Locale;

@Component
public abstract class BaseAppointmentFactory implements AppointmentFactory {

    protected Appointment base(AppointmentRequest request, Long userId) {
        var now = LocalDateTime.now();

        Appointment a = new Appointment();
        a.setInstitutionId(request.getInstitutionId());
        a.setUserId(userId);
        a.setInstitutionType(request.getInstitutionType());
        a.setNotes(request.getNotes());
        a.setAppointmentTime(request.getAppointmentTime());
        a.setDocumentRequired(request.getDocumentRequired());
        a.setReminderSent(false);
        a.setCreatedAt(now);
        a.setUpdatedAt(now);

        // title default format
        a.setTitle("Programare " + request.getServiceType() + " la instituția " + request.getInstitutionId());

        // priority default
        a.setPriorityLevel(parsePriorityLevel(request.getPriorityLevel()));
        return a;
    }

    protected double applyPriorityAdjustment(double baseDuration, Appointment.PriorityLevel priority) {
        if (priority == Appointment.PriorityLevel.URGENT) {
            return baseDuration * 0.8;
        }
        return baseDuration;
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