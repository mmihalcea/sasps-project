package edu.saspsproject.service;

import edu.saspsproject.dto.request.AppointmentRequest;
import edu.saspsproject.dto.response.*;
import edu.saspsproject.factory.AppointmentFactoryProvider;
import edu.saspsproject.model.Appointment;
import edu.saspsproject.model.Institution;
import edu.saspsproject.model.User;
import edu.saspsproject.repository.AppointmentRepository;
import edu.saspsproject.repository.CountyRepository;
import edu.saspsproject.repository.InstitutionRepository;
import edu.saspsproject.repository.UserRepository;
import edu.saspsproject.service.validation.AppointmentValidator;
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

    private final AppointmentFactoryProvider appointmentFactoryProvider;
    private final AppointmentRepository appointmentRepository;
    private final InstitutionRepository institutionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final CountyRepository countyRepository;
    private final EmailService emailService;
    private final AppointmentValidator appointmentValidator;


    public Long saveAppointment(AppointmentRequest request) {
        User user = findOrCreateUser(request);

        Appointment.ServiceType serviceType;
        try {
            serviceType = Appointment.ServiceType.valueOf(request.getServiceType().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid service type: " + request.getServiceType());
        }

        Appointment appointment = appointmentFactoryProvider
                .get(serviceType)
                .create(request, user.getId());

        //validate using strategies
        appointmentValidator.validate(appointment);

        // Set priority and status based on complex rules, no Strategy pattern
        setPriorityAndStatus(appointment);

        // Save in DB
        Appointment saved = appointmentRepository.save(appointment);

        // Send different notifications based on institution type, hardcoded without Adapter pattern
        sendNotifications(saved);

        return saved.getId();
    }

    private User findOrCreateUser(AppointmentRequest request) {
        return userRepository.findByEmail(request.getCustomerEmail())
                .map(existing -> {
                    // update name/phone if changed
                    existing.setName(request.getCustomerName());
                    existing.setPhone(request.getCustomerPhone());
                    return userRepository.save(existing);
                })
                .orElseGet(() -> {
                    User user = new User();
                    user.setName(request.getCustomerName());
                    user.setEmail(request.getCustomerEmail());
                    user.setPhone(request.getCustomerPhone());
                    return userRepository.save(user);
                });
    }

    private void setPriorityAndStatus(Appointment appointment) {
        if (appointment.getPriorityLevel() == Appointment.PriorityLevel.URGENT) {
            appointment.setStatus(Appointment.Status.CONFIRMED);
        } else {
            appointment.setStatus(Appointment.Status.PENDING);
        }
    }

    private void sendNotifications(Appointment appointment) {
        // Hardcoded notification logic, should use Adapter pattern in v2
        notificationService.sendConfirmation(appointment);
        
        // Tightly coupled email sending, no event-driven architecture
        try {
            User user = userRepository.findById(appointment.getUserId()).orElse(null);
            if (user != null && user.getEmailNotificationsEnabled()) {
                Institution institution = institutionRepository.findById(appointment.getInstitutionId()).orElse(null);
                String institutionName = institution != null ? institution.getName() : "Instituție necunoscută";
                emailService.sendAppointmentConfirmationEmail(user, appointment, institutionName);
            }
        } catch (Exception e) {
            System.err.println("Failed to send confirmation email: " + e.getMessage());
        }
    }

    public AvailabilityResponse getAvailability(Long institutionId, Optional<LocalDate> startDate) {
        Institution institution = institutionRepository.findById(institutionId)
                .orElseThrow(() -> new IllegalArgumentException("Institution not found"));

        // Generate slots based on institution-specific rules, hardcoded
        List<LocalDateTime> allSlots = generateAvailableSlots(institution, startDate);

        // Get all booked slots
        Set<LocalDateTime> bookedSlots = appointmentRepository.findByInstitutionId(institutionId)
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

    private List<LocalDateTime> generateAvailableSlots(Institution institution, Optional<LocalDate> startDateRequest) {
        List<LocalDateTime> slots = new ArrayList<>();
        LocalDate startDate = startDateRequest.orElseGet(() -> LocalDate.now().plusDays(1));

        for (int day = 0; day < 14; day++) {
            LocalDate currentDate = startDate.plusDays(day);

            boolean weekend = currentDate.getDayOfWeek().getValue() == 6
                    || currentDate.getDayOfWeek().getValue() == 7;

            // Skip weekends for government institutions, hardcoded logic
            if (weekend && (institution.getType() == Institution.InstitutionType.PRIMARIA
                    || institution.getType() == Institution.InstitutionType.ANAF)) {
                continue;
            }

            LocalTime currentTime = institution.getOpeningTime();
            while (currentTime.isBefore(institution.getClosingTime())) {
                slots.add(LocalDateTime.of(currentDate, currentTime));

                // Different slot intervals based on institution type,- hardcoded
                if (institution.getType() == Institution.InstitutionType.ANAF) {
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
        return userRepository.findByEmail(email)
                .map(user -> appointmentRepository.findByUserId(user.getId()))
                .orElse(Collections.emptyList());
    }

    public List<Institution> getAllInstitutions() {
        return institutionRepository.findAll();
    }

    public List<InstitutionResponse> getInstitutionsByCounty(Long countyId) {
        return institutionRepository.findByCountyIdOrCountyIdIsNull(countyId).stream().map(institution -> new InstitutionResponse(institution.getId(), institution.getName(), institution.getType().toString())).collect(Collectors.toList());
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Map<String, Object> getGlobalStats() {
        List<Appointment> all = appointmentRepository.findAll();
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalAppointments", all.size());
        stats.put("totalInstitutions", institutionRepository.count());

        // Group by institution
        Map<Long, Long> byInstitution = all.stream()
                .collect(Collectors.groupingBy(
                        Appointment::getInstitutionId,
                        Collectors.counting()
                ));
        stats.put("appointmentsByInstitution", byInstitution);

        // Group by service type
        Map<Appointment.ServiceType, Long> byService = all.stream()
                .filter(a -> a.getServiceType() != null)
                .collect(Collectors.groupingBy(
                        Appointment::getServiceType,
                        Collectors.counting()
                ));
        stats.put("appointmentsByService", byService);

        return stats;
    }

    public Map<String, Object> getInstitutionStats(Long institutionId) {
        List<Appointment> appointments = appointmentRepository.findByInstitutionId(institutionId);
        Institution institution = institutionRepository.findById(institutionId).orElse(null);

        Map<String, Object> stats = new HashMap<>();
        stats.put("institutionId", institutionId);
        stats.put("institutionName", institution != null ? institution.getName() : "Unknown");
        stats.put("totalAppointments", appointments.size());

        // Group by service type for this institution
        Map<Appointment.ServiceType, Long> byService = appointments.stream()
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

            long daysBetween = Math.max(1, firstDate.until(lastDate).getDays() + 1);
            double avgPerDay = (double) appointments.size() / daysBetween;
            stats.put("averageAppointmentsPerDay", avgPerDay);
        }

        return stats;
    }

    public List<Appointment> getAppointmentsByDate(String dateString) {
        LocalDate date;
        try {
            date = LocalDate.parse(dateString);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Use YYYY-MM-DD");
        }

        return appointmentRepository.findAll().stream()
                .filter(a -> a.getAppointmentTime().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }

    public List<Appointment> getAppointmentsByService(String serviceType) {
        // parsing now lives in factory, so simplest is:
        Appointment.ServiceType parsed;
        try {
            parsed = Appointment.ServiceType.valueOf(serviceType.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid service type: " + serviceType);
        }

        Appointment.ServiceType finalParsed = parsed;
        return appointmentRepository.findAll().stream()
                .filter(a -> finalParsed == a.getServiceType())
                .collect(Collectors.toList());
    }

    public List<CountyResponse> getAllCounties() {
        return countyRepository.findAll().stream()
                .map(county -> new CountyResponse(county.getId(), county.getName()))
                .collect(Collectors.toList());
    }

    // Cancel appointment with email notification, tightly coupled
    public void cancelAppointment(Long appointmentId, String reason) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(Appointment.Status.CANCELLED);
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);

        // Hardcoded notification sending, no event system
        try {
            User user = userRepository.findById(appointment.getUserId()).orElse(null);
            if (user != null && user.getEmailNotificationsEnabled()) {
                Institution institution = institutionRepository.findById(appointment.getInstitutionId()).orElse(null);
                String institutionName = institution != null ? institution.getName() : "Instituție necunoscută";
                emailService.sendAppointmentCancellationEmail(user, appointment, institutionName, reason);
            }

            notificationService.createNotification(
                    appointment.getUserId(),
                    "Programarea dumneavoastră a fost anulată" + (reason != null ? ": " + reason : ""),
                    "CANCELLATION"
            );
        } catch (Exception e) {
            System.err.println("Failed to send cancellation notifications: " + e.getMessage());
        }
    }

    // Confirm appointment
    public void confirmAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(Appointment.Status.CONFIRMED);
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);

        try {
            notificationService.createNotification(
                    appointment.getUserId(),
                    "Programarea dumneavoastră a fost confirmată",
                    "CONFIRMATION"
            );
        } catch (Exception e) {
            System.err.println("Failed to send confirmation notification: " + e.getMessage());
        }
    }

    // Complete appointment
    public void completeAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(Appointment.Status.COMPLETED);
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);

        try {
            notificationService.createNotification(
                    appointment.getUserId(),
                    "Programarea dumneavoastră a fost finalizată",
                    "COMPLETION"
            );
        } catch (Exception e) {
            System.err.println("Failed to send completion notification: " + e.getMessage());
        }
    }
}