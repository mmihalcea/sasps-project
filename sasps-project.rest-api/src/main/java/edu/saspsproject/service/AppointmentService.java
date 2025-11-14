package edu.saspsproject.service;

import edu.saspsproject.dto.AppointmentRequest;
import edu.saspsproject.dto.AvailabilityResponse;
import edu.saspsproject.model.Appointment;
import edu.saspsproject.model.Institution;
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
        // Complex validation logic - hardcoded without Validator pattern
        validateAppointmentRequest(appointmentRequest);
        
        Institution institution = repository.findInstitutionById(appointmentRequest.getInstitutionId());
        if (institution == null) {
            throw new IllegalArgumentException("Institution not found");
        }

        // Business rules validation - hardcoded without Strategy pattern
        validateBusinessRules(appointmentRequest, institution);

        // Create appointment with complex logic - no Factory pattern
        Appointment appointment = createAppointmentFromRequest(appointmentRequest, institution);
        
        // Calculate estimated duration based on service type - hardcoded
        calculateEstimatedDuration(appointment);
        
        // Set priority and status based on complex rules - no Strategy pattern
        setPriorityAndStatus(appointment, institution);

        Long id = repository.save(appointment);
        
        // Send different notifications based on institution type - hardcoded without Adapter pattern
        sendNotifications(appointment, institution);

        return id;
    }

    private void validateAppointmentRequest(AppointmentRequest request) {
        if (request.getInstitutionId() == null) {
            throw new IllegalArgumentException("Institution ID is required");
        }
        if (request.getAppointmentTime() == null) {
            throw new IllegalArgumentException("Appointment time is required");
        }
        if (request.getCustomerName() == null || request.getCustomerName().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        if (request.getCustomerEmail() == null || !request.getCustomerEmail().contains("@")) {
            throw new IllegalArgumentException("Valid email is required");
        }
    }

    private void validateBusinessRules(AppointmentRequest request, Institution institution) {
        // Check if appointment is in the past
        if (request.getAppointmentTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot book appointments in the past");
        }

        // Check if appointment is within institution hours
        LocalTime appointmentTime = request.getAppointmentTime().toLocalTime();
        if (appointmentTime.isBefore(institution.getOpeningTime()) || 
            appointmentTime.isAfter(institution.getClosingTime())) {
            throw new IllegalArgumentException("Appointment must be within institution hours");
        }
    }

    private Appointment createAppointmentFromRequest(AppointmentRequest request, Institution institution) {
        Appointment appointment = new Appointment();
        appointment.setInstitutionId(request.getInstitutionId());
        appointment.setInstitutionType(request.getInstitutionType());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setCustomerName(request.getCustomerName());
        appointment.setCustomerEmail(request.getCustomerEmail());
        appointment.setCustomerPhone(request.getCustomerPhone());
        appointment.setServiceType(request.getServiceType());
        appointment.setPriorityLevel(request.getPriorityLevel() != null ? request.getPriorityLevel() : "NORMAL");
        appointment.setNotes(request.getNotes());
        appointment.setDocumentRequired(request.getDocumentRequired());
        return appointment;
    }

    private void calculateEstimatedDuration(Appointment appointment) {
        // Hardcoded duration calculation - should use Strategy pattern in v2
        double baseDuration;
        switch (appointment.getServiceType()) {
            case "ELIBERARE_CI":
                baseDuration = 30.0;
                break;
            case "CERTIFICAT_NASTERE":
                baseDuration = 20.0;
                break;
            case "DECLARATIE_FISCALA":
                baseDuration = 45.0;
                break;
            default:
                baseDuration = 30.0;
        }
        
        // Add extra time based on priority
        if ("URGENT".equals(appointment.getPriorityLevel())) {
            baseDuration *= 0.8;
        }
        
        appointment.setEstimatedDuration(baseDuration);
    }

    private void setPriorityAndStatus(Appointment appointment, Institution institution) {
        // Complex priority and status logic - should use Strategy pattern in v2
        if ("URGENT".equals(appointment.getPriorityLevel())) {
            appointment.setStatus("CONFIRMED");
        } else {
            appointment.setStatus("PENDING");
        }
    }

    private void sendNotifications(Appointment appointment, Institution institution) {
        // Hardcoded notification logic - should use Adapter pattern in v2
        notificationService.sendConfirmation(appointment);
    }

    public AvailabilityResponse getAvailability(Long institutionId) {
        Institution institution = repository.findInstitutionById(institutionId);
        if (institution == null) {
            throw new IllegalArgumentException("Institution not found");
        }

        // Generate slots based on institution-specific rules - hardcoded
        List<LocalDateTime> allSlots = generateAvailableSlots(institution);
        
        // Get all booked slots
        Set<LocalDateTime> bookedSlots = repository.findByInstitutionId(institutionId)
                .stream()
                .map(Appointment::getAppointmentTime)
                .collect(Collectors.toSet());

        // Filter available slots
        List<LocalDateTime> availableSlots = allSlots.stream()
                .filter(slot -> slot.isAfter(LocalDateTime.now().plusHours(2)))
                .filter(slot -> !bookedSlots.contains(slot))
                .collect(Collectors.toList());

        return new AvailabilityResponse(institutionId, availableSlots);
    }

    private List<LocalDateTime> generateAvailableSlots(Institution institution) {
        List<LocalDateTime> slots = new ArrayList<>();
        LocalDate startDate = LocalDate.now().plusDays(1);
        
        for (int day = 0; day < 14; day++) {
            LocalDate currentDate = startDate.plusDays(day);
            
            // Skip weekends for government institutions - hardcoded logic
            if ((currentDate.getDayOfWeek().getValue() == 6 || currentDate.getDayOfWeek().getValue() == 7) &&
                ("PRIMARIA".equals(institution.getType()) || "ANAF".equals(institution.getType()))) {
                continue;
            }
            
            LocalTime currentTime = institution.getOpeningTime();
            while (currentTime.isBefore(institution.getClosingTime())) {
                slots.add(LocalDateTime.of(currentDate, currentTime));
                
                // Different slot intervals based on institution type - hardcoded
                if ("ANAF".equals(institution.getType())) {
                    currentTime = currentTime.plusMinutes(45);
                } else {
                    currentTime = currentTime.plusMinutes(30);
                }
            }
        }
        
        return slots;
    }

    // Additional methods that show complexity without patterns
    public List<Appointment> getCustomerAppointments(String email) {
        return repository.findByCustomerEmail(email);
    }

    public List<Institution> getAllInstitutions() {
        return repository.findAllInstitutions();
    }

    public List<edu.saspsproject.model.Service> getServicesByInstitutionType(String institutionType) {
        return repository.findServicesByInstitutionType(institutionType);
    }

    public List<Appointment> getAllAppointments() {
        return repository.findAll();
    }

    public Map<String, Object> getGlobalStats() {
        List<Appointment> allAppointments = repository.findAll();
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalAppointments", allAppointments.size());
        stats.put("totalInstitutions", repository.findAllInstitutions().size());
        
        // Group by institution
        Map<Long, Long> byInstitution = allAppointments.stream()
            .collect(Collectors.groupingBy(
                Appointment::getInstitutionId,
                Collectors.counting()
            ));
        stats.put("appointmentsByInstitution", byInstitution);
        
        // Group by service type
        Map<String, Long> byService = allAppointments.stream()
            .filter(a -> a.getServiceType() != null)
            .collect(Collectors.groupingBy(
                Appointment::getServiceType,
                Collectors.counting()
            ));
        stats.put("appointmentsByService", byService);
        
        return stats;
    }

    public Map<String, Object> getInstitutionStats(Long institutionId) {
        List<Appointment> appointments = repository.findByInstitutionId(institutionId);
        Institution institution = repository.findInstitutionById(institutionId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("institutionId", institutionId);
        stats.put("institutionName", institution != null ? institution.getName() : "Unknown");
        stats.put("totalAppointments", appointments.size());
        
        // Group by service type for this institution
        Map<String, Long> byService = appointments.stream()
            .filter(a -> a.getServiceType() != null)
            .collect(Collectors.groupingBy(
                Appointment::getServiceType,
                Collectors.counting()
            ));
        stats.put("appointmentsByService", byService);
        
        // Calculate average appointments per day
        if (!appointments.isEmpty()) {
            LocalDate firstDate = appointments.stream()
                .map(a -> a.getAppointmentTime().toLocalDate())
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());
            LocalDate lastDate = appointments.stream()
                .map(a -> a.getAppointmentTime().toLocalDate())
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());
            
            long daysBetween = firstDate.until(lastDate).getDays() + 1;
            double avgPerDay = (double) appointments.size() / daysBetween;
            stats.put("averageAppointmentsPerDay", avgPerDay);
        }
        
        return stats;
    }

    public List<Appointment> getAppointmentsByDate(String dateString) {
        try {
            LocalDate date = LocalDate.parse(dateString);
            return repository.findAll().stream()
                .filter(a -> a.getAppointmentTime().toLocalDate().equals(date))
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Use YYYY-MM-DD");
        }
    }

    public List<Appointment> getAppointmentsByService(String serviceType) {
        return repository.findAll().stream()
            .filter(a -> serviceType.equals(a.getServiceType()))
            .collect(Collectors.toList());
    }
}