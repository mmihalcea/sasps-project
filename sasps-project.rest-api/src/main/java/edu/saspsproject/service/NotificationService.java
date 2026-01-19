package edu.saspsproject.service;

import edu.saspsproject.adapter.SmsProviderFactory;
import edu.saspsproject.model.Appointment;
import edu.saspsproject.model.Institution;
import edu.saspsproject.model.Notification;
import edu.saspsproject.model.User;
import edu.saspsproject.repository.NotificationRepository;
import edu.saspsproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final SmsProviderFactory smsProviderFactory;

    public void sendConfirmation(Appointment appointment) {
        String email = getEmail(appointment);
        if (email != null) {
            log.info("Sending basic confirmation to {} for appointment {} at {}",
                    email,
                    appointment.getId(),
                    appointment.getAppointmentTime());
        }
        smsProviderFactory.sendSms(userRepository.findById(appointment.getUserId()).get().getPhone(), String.format("Programare confirmata: Serviciul: %s, data: %s, ora: %s", appointment.getServiceType().toString(), appointment.getAppointmentTime().toLocalDate().toString(), appointment.getAppointmentTime().toLocalTime().toString()));
    }

    // Hardcoded email notification logic - should use Adapter pattern in v2
    public void sendEmailConfirmation(Appointment appointment, Institution institution) {
        String emailContent = generateEmailContent(appointment, institution);
        String recipientEmail = getEmail(appointment);
        log.info("Sending EMAIL confirmation to: {}", recipientEmail);
        log.info("Email content: {}", emailContent);

        // Simulate different email providers based on institution - hardcoded
        Institution.InstitutionType institutionType = institution.getType();
        if (institutionType == Institution.InstitutionType.ANAF) {
            sendViaGovEmailProvider(recipientEmail, emailContent);
        } else if (institutionType == Institution.InstitutionType.PRIMARIA) {
            sendViaLocalGovProvider(recipientEmail, emailContent);
        } else {
            sendViaGenericProvider(recipientEmail, emailContent);
        }
    }

    // Hardcoded SMS notification logic - should use Adapter pattern in v2
    public void sendSMSConfirmation(Appointment appointment, Institution institution) {
        String smsContent = generateSMSContent(appointment, institution);
        String recipientPhone = getPhone(appointment);
        log.info("Sending SMS confirmation to: {}", recipientPhone);
        log.info("SMS content: {}", smsContent);

        // Simulate different SMS providers - hardcoded logic
        if (recipientPhone != null) {
            if (recipientPhone.startsWith("07")) {
                sendViaOrangeSMS(recipientPhone, smsContent);
            } else if (recipientPhone.startsWith("06")) {
                sendViaVodafoneSMS(recipientPhone, smsContent);
            } else {
                sendViaTelekomSMS(recipientPhone, smsContent);
            }
        }
    }

    // Hardcoded urgent notification logic
    public void sendUrgentNotification(Appointment appointment) {
        String recipientPhone = getPhone(appointment);
        String recipientEmail = getEmail(appointment);
        log.warn("URGENT: Sending priority notification for appointment {} to {}",
                appointment.getId(), recipientEmail);

        // Send both email and SMS for urgent cases - hardcoded logic
        String urgentMessage = "URGENT: Your appointment has been confirmed with high priority. " +
                "Please arrive 15 minutes early. Appointment ID: " + appointment.getId();

        if (recipientEmail != null) {
            sendViaGenericProvider(recipientEmail, urgentMessage);
        }

        if (recipientPhone != null) {
            sendViaOrangeSMS(recipientPhone, urgentMessage);
        }
    }

    // Hardcoded consumer rights notification
    public void sendConsumerRightsInfo(Appointment appointment) {
        String rightsInfo = "Important: As a consumer, you have the right to: " +
                "1) Receive quality services, 2) File complaints, 3) Request mediation. " +
                "Your appointment: " + appointment.getId() + " at " + appointment.getAppointmentTime();

        log.info("Sending consumer rights info to: {}", getEmail(appointment));
        sendViaGenericProvider(getEmail(appointment), rightsInfo);
    }

    // Hardcoded cancellation notification
    public void sendCancellationNotification(Appointment appointment, Institution institution) {
        String subject = "Appointment Cancelled - " + institution.getName();
        String message = String.format("Dear %s,\n\nYour appointment (ID: %d) scheduled for %s has been cancelled.\n\n" +
                "Institution: %s\nService: %s\n\nPlease reschedule at your convenience.",
                getCustomerName(appointment),
                appointment.getId(),
                appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                institution.getName(),
                appointment.getServiceType());

        log.info("Sending cancellation notification to: {}", getEmail(appointment));
        sendViaGenericProvider(getEmail(appointment), subject + "\n\n" + message);
    }

    // Hardcoded reschedule notification
    public void sendRescheduleNotification(Appointment appointment, LocalDateTime oldTime, LocalDateTime newTime) {
        String message = String.format("Dear %s,\n\nYour appointment has been rescheduled:\n\n" +
                "OLD TIME: %s\nNEW TIME: %s\n\nAppointment ID: %d\nService: %s",
                getCustomerName(appointment),
                oldTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                newTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                appointment.getId(),
                appointment.getServiceType());

        log.info("Sending reschedule notification to: {}", getEmail(appointment));
        sendViaGenericProvider(getEmail(appointment), message);
    }

    // Hardcoded content generation - should use Template Method pattern in v2
    private String generateEmailContent(Appointment appointment, Institution institution) {
        StringBuilder content = new StringBuilder();

        content.append("Dear ").append(getCustomerName(appointment)).append(",\n\n");
        content.append("Your appointment has been confirmed:\n\n");
        content.append("Institution: ").append(institution.getName()).append("\n");
        content.append("Address: ").append(institution.getAddress()).append("\n");
        content.append("Date & Time: ").append(appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        content.append("Service: ").append(appointment.getServiceType()).append("\n");
        content.append("Estimated Duration: ").append(appointment.getEstimatedDuration()).append(" minutes\n");
        content.append("Status: ").append(appointment.getStatus()).append("\n");
        content.append("Priority: ").append(appointment.getPriorityLevel()).append("\n\n");

        // Add institution-specific instructions - hardcoded logic
        Institution.InstitutionType institutionType = institution.getType();
        if (institutionType == Institution.InstitutionType.PRIMARIA) {
            content.append("Required documents: ID card, proof of residence\n");
            content.append("Please arrive 15 minutes early for document verification.\n");
        } else if (institutionType == Institution.InstitutionType.ANAF) {
            content.append("Required documents: Tax declaration, supporting documents\n");
            content.append("Please bring original documents and copies.\n");
            content.append("Note: Our office has a lunch break from 12:00-13:00\n");
        } else if (institutionType == Institution.InstitutionType.ANPC) {
            content.append("Please bring any relevant documentation for your case\n");
            content.append("Consumer protection services are free of charge\n");
        }

        content.append("\nAppointment ID: ").append(appointment.getId()).append("\n");
        content.append("Contact: ").append(institution.getPhone()).append("\n\n");
        content.append("Best regards,\n").append(institution.getName());

        return content.toString();
    }

    private String generateSMSContent(Appointment appointment, Institution institution) {
        return String.format("Appointment confirmed at %s on %s. ID: %d. Address: %s. Contact: %s",
                institution.getName(),
                appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")),
                appointment.getId(),
                institution.getAddress(),
                institution.getPhone());
    }

    // Simulate different email providers - hardcoded implementations
    private void sendViaGovEmailProvider(String email, String content) {
        log.info("[GOV-EMAIL] Sending to {} via government email service", email);
        // Simulate government email service specifics
        log.debug("Using secure government SMTP with encryption...");
    }

    private void sendViaLocalGovProvider(String email, String content) {
        log.info("[LOCAL-GOV] Sending to {} via local government provider", email);
        // Simulate local government email service
        log.debug("Using local authority email system...");
    }

    private void sendViaGenericProvider(String email, String content) {
        log.info("[GENERIC] Sending to {} via generic email service", email);
        // Simulate generic email service
        log.debug("Using standard SMTP service...");
    }

    // Simulate different SMS providers - hardcoded implementations
    private void sendViaOrangeSMS(String phone, String content) {
        log.info("[ORANGE-SMS] Sending SMS to {} via Orange", phone);
        log.debug("Using Orange SMS API...");
    }

    private void sendViaVodafoneSMS(String phone, String content) {
        log.info("[VODAFONE-SMS] Sending SMS to {} via Vodafone", phone);
        log.debug("Using Vodafone SMS gateway...");
    }

    private void sendViaTelekomSMS(String phone, String content) {
        log.info("[TELEKOM-SMS] Sending SMS to {} via Telekom", phone);
        log.debug("Using Telekom messaging service...");
    }

    // Helpers to read user data from appointment
    private String getEmail(Appointment appointment) {
        if (appointment == null || appointment.getUserId() == null) return null;
        return userRepository.findById(appointment.getUserId())
                .map(User::getEmail)
                .orElse(null);
    }

    private String getPhone(Appointment appointment) {
        if (appointment == null || appointment.getUserId() == null) return null;
        return userRepository.findById(appointment.getUserId())
                .map(User::getPhone)
                .orElse(null);
    }

    private String getCustomerName(Appointment appointment) {
        if (appointment == null || appointment.getUserId() == null) return "Customer";
        return userRepository.findById(appointment.getUserId())
                .map(User::getName)
                .orElse("Customer");
    }

    // Simple notification creation - NOW with database storage!
    // No event system, just direct database insert - baseline approach
    public void createNotification(Long userId, String message, String type) {
        log.info("Creating notification for user {}: [{}] {}", userId, type, message);
        
        // Create and save notification entity - hardcoded logic
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        
        // Map string type to enum - hardcoded mapping
        switch (type.toUpperCase()) {
            case "CONFIRMATION" -> notification.setType(Notification.NotificationType.CONFIRMATION);
            case "REMINDER" -> notification.setType(Notification.NotificationType.REMINDER);
            case "CANCELLATION" -> notification.setType(Notification.NotificationType.CANCELLATION);
            case "WELCOME" -> notification.setType(Notification.NotificationType.WELCOME);
            default -> notification.setType(Notification.NotificationType.ANNOUNCEMENT);
        }
        
        notification.setMethod(Notification.NotificationMethod.EMAIL);
        notification.setStatus(Notification.NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.now());
        
        // Get user email - hardcoded lookup
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            notification.setRecipientEmail(user.getEmail());
            notification.setRecipientPhone(user.getPhone());
        }
        
        notificationRepository.save(notification);
        System.out.println(String.format("[NOTIFICATION][%s] Saved to DB - User %d: %s", type, userId, message));
    }
    
    // Get all notifications for a user
    public java.util.List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserId(userId);
    }
    
    // Get all notifications (admin only)
    public java.util.List<Notification> getAllNotifications() {
        return notificationRepository.findAllByOrderBySentAtDesc();
    }
    
    // Get notifications by status
    public java.util.List<Notification> getNotificationsByStatus(Notification.NotificationStatus status) {
        return notificationRepository.findByStatusOrderBySentAtDesc(status);
    }
}