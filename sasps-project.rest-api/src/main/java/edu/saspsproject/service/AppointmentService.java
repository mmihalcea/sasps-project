package edu.saspsproject.service;

import edu.saspsproject.dto.AppointmentRequest;
import edu.saspsproject.dto.AvailabilityResponse;
import edu.saspsproject.model.Appointment;
import edu.saspsproject.repository.InMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final InMemoryRepository repository;
    private final NotificationService notificationService;

    public Long saveAppointment(AppointmentRequest appointmentRequest) {
        // basic validation
        if (appointmentRequest.getInstitutionId() == null || appointmentRequest.getAppointmentTime() == null) {
            throw new IllegalArgumentException("institutionId and appointmentTime are required");
        }

        // conflict check
        boolean conflict = repository.findByInstitutionId(appointmentRequest.getInstitutionId())
                .stream()
                .anyMatch(a -> a.getAppointmentTime().equals(appointmentRequest.getAppointmentTime()));

        if (conflict) {
            throw new IllegalStateException("Requested slot is already booked");
        }

        Appointment appt = new Appointment();
        appt.setInstitutionId(appointmentRequest.getInstitutionId());
        appt.setAppointmentTime(appointmentRequest.getAppointmentTime());
        appt.setCustomerName(appointmentRequest.getCustomerName());
        appt.setCustomerEmail(appointmentRequest.getCustomerEmail());

        Long id = repository.save(appt);
        // notify (simulated)
        notificationService.sendConfirmation(appt);

        return id;
    }

    public AvailabilityResponse getAvailability(Long institutionId) {
        // build candidate slots: next 7 days, 9:00-16:00 every 30 minutes
        LocalDate start = LocalDate.now();
        int days = 7;
        List<LocalDateTime> allSlots = new ArrayList<>();
        for (int d = 0; d < days; d++) {
            LocalDate day = start.plusDays(d);
            LocalTime t = LocalTime.of(9, 0);
            while (!t.isAfter(LocalTime.of(16, 0))) {
                allSlots.add(LocalDateTime.of(day, t));
                t = t.plusMinutes(30);
            }
        }

        Set<LocalDateTime> booked = repository.findByInstitutionId(institutionId)
                .stream()
                .map(a -> a.getAppointmentTime())
                .collect(Collectors.toSet());

        List<LocalDateTime> available = allSlots.stream()
                .filter(slot -> slot.isAfter(LocalDateTime.now()))
                .filter(slot -> !booked.contains(slot))
                .collect(Collectors.toList());

        return new AvailabilityResponse(institutionId, available);
    }
}