package edu.saspsproject.service;

import edu.saspsproject.model.Appointment;
import edu.saspsproject.model.User;
import edu.saspsproject.repository.AppointmentRepository;
import edu.saspsproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Reminder service without design patterns - tightly coupled, hardcoded logic
 * No Observer pattern, no Strategy pattern for different reminder types
 */
@Service
public class ReminderService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    @Value("${app.reminder.hours-before:24}")
    private int reminderHoursBefore;

    @Value("${app.reminder.enabled:true}")
    private boolean reminderEnabled;

    public ReminderService(AppointmentRepository appointmentRepository,
                          UserRepository userRepository,
                          EmailService emailService,
                          NotificationService notificationService) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    // Runs every hour - hardcoded schedule, no flexible configuration
    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    public void sendAppointmentReminders() {
        if (!reminderEnabled) {
            return;
        }

        System.out.println("Starting reminder check at: " + LocalDateTime.now());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderTimeStart = now.plusHours(reminderHoursBefore);
        LocalDateTime reminderTimeEnd = reminderTimeStart.plusHours(1);

        // Hardcoded query logic - no specification pattern
        List<Appointment> upcomingAppointments = appointmentRepository.findAll().stream()
                .filter(apt -> apt.getAppointmentTime().isAfter(reminderTimeStart) 
                        && apt.getAppointmentTime().isBefore(reminderTimeEnd))
                .filter(apt -> apt.getStatus() == Appointment.Status.CONFIRMED 
                        || apt.getStatus() == Appointment.Status.PENDING)
                .toList();

        System.out.println("Found " + upcomingAppointments.size() + " appointments needing reminders");

        // Tightly coupled logic - directly calling multiple services
        for (Appointment appointment : upcomingAppointments) {
            try {
                // Hardcoded user lookup - no repository abstraction
                Long userId = appointment.getUserId();
                if (userId == null) {
                    continue;
                }

                User user = userRepository.findById(userId).orElse(null);
                if (user == null || !user.getActive()) {
                    continue;
                }

                // Hardcoded institution name extraction
                String institutionName = getInstitutionNameFromAppointment(appointment);

                // Directly calling email service - no notification strategy
                if (user.getEmailNotificationsEnabled()) {
                    emailService.sendAppointmentReminderEmail(user, appointment, institutionName);
                }

                // Hardcoded notification creation
                createReminderNotification(user, appointment, institutionName);

                // Could add SMS here - more duplication
                if (user.getSmsNotificationsEnabled() && user.getPhone() != null) {
                    sendSmsReminder(user, appointment, institutionName);
                }

                System.out.println("Reminder sent for appointment ID: " + appointment.getId());
            } catch (Exception e) {
                System.err.println("Failed to send reminder for appointment " + appointment.getId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Hardcoded SMS logic - no adapter pattern
    private void sendSmsReminder(User user, Appointment appointment, String institutionName) {
        // Simulated SMS sending - in real app would integrate with SMS provider
        String smsMessage = String.format(
                "Reminder: Aveți programare la %s pe %s. SASPS",
                institutionName,
                appointment.getAppointmentTime().toString()
        );
        System.out.println("SMS sent to " + user.getPhone() + ": " + smsMessage);
    }

    // Duplicated institution name logic - also exists in AppointmentService
    private String getInstitutionNameFromAppointment(Appointment appointment) {
        // Hardcoded logic - no clean separation
        String type = appointment.getInstitutionType();
        if (type == null) {
            return "Instituție necunoscută";
        }

        return switch (type) {
            case "PRIMARIE" -> "Primăria " + appointment.getNotes();
            case "DRPCIV" -> "DRPCIV";
            case "ANAF" -> "ANAF";
            case "CASA_DE_PENSII" -> "Casa de Pensii";
            default -> "Instituție Publică";
        };
    }

    // Direct notification service call - no event-driven architecture
    private void createReminderNotification(User user, Appointment appointment, String institutionName) {
        try {
            String message = String.format(
                    "Reminder: Aveți programare la %s pe %s",
                    institutionName,
                    appointment.getAppointmentTime().toString()
            );
            notificationService.createNotification(user.getId(), message, "REMINDER");
        } catch (Exception e) {
            System.err.println("Failed to create reminder notification: " + e.getMessage());
        }
    }

    // Manual reminder trigger - no command pattern
    public void sendManualReminder(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        if (appointment == null) {
            throw new RuntimeException("Appointment not found");
        }

        User user = userRepository.findById(appointment.getUserId()).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        String institutionName = getInstitutionNameFromAppointment(appointment);

        // Duplicated sending logic
        if (user.getEmailNotificationsEnabled()) {
            emailService.sendAppointmentReminderEmail(user, appointment, institutionName);
        }

        if (user.getSmsNotificationsEnabled() && user.getPhone() != null) {
            sendSmsReminder(user, appointment, institutionName);
        }

        createReminderNotification(user, appointment, institutionName);
    }

    // Hardcoded batch reminder for multiple users - no iterator pattern
    public void sendBatchReminders(List<Long> appointmentIds) {
        for (Long appointmentId : appointmentIds) {
            try {
                sendManualReminder(appointmentId);
            } catch (Exception e) {
                System.err.println("Failed batch reminder for appointment " + appointmentId);
            }
        }
    }
}
