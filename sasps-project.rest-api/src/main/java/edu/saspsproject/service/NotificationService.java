package edu.saspsproject.service;

import edu.saspsproject.model.Appointment;
import edu.saspsproject.model.Institution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class NotificationService {
    
    public void sendConfirmation(Appointment appointment) {
        if (appointment.getCustomerEmail() != null) {
            log.info("Sending basic confirmation to {} for appointment {} at {}",
                    appointment.getCustomerEmail(),
                    appointment.getId(),
                    appointment.getAppointmentTime());
        }
    }

    // Hardcoded email notification logic - should use Adapter pattern in v2
    public void sendEmailConfirmation(Appointment appointment, Institution institution) {
        String emailContent = generateEmailContent(appointment, institution);
        log.info("Sending EMAIL confirmation to: {}", appointment.getCustomerEmail());
        log.info("Email content: {}", emailContent);
        
        // Simulate different email providers based on institution - hardcoded
        if ("ANAF".equals(institution.getType())) {
            sendViaGovEmailProvider(appointment.getCustomerEmail(), emailContent);
        } else if ("PRIMARIA".equals(institution.getType())) {
            sendViaLocalGovProvider(appointment.getCustomerEmail(), emailContent);
        } else {
            sendViaGenericProvider(appointment.getCustomerEmail(), emailContent);
        }
    }

    // Hardcoded SMS notification logic - should use Adapter pattern in v2
    public void sendSMSConfirmation(Appointment appointment, Institution institution) {
        String smsContent = generateSMSContent(appointment, institution);
        log.info("Sending SMS confirmation to: {}", appointment.getCustomerPhone());
        log.info("SMS content: {}", smsContent);
        
        // Simulate different SMS providers - hardcoded logic
        if (appointment.getCustomerPhone() != null) {
            if (appointment.getCustomerPhone().startsWith("07")) {
                sendViaOrangeSMS(appointment.getCustomerPhone(), smsContent);
            } else if (appointment.getCustomerPhone().startsWith("06")) {
                sendViaVodafoneSMS(appointment.getCustomerPhone(), smsContent);
            } else {
                sendViaTelekomSMS(appointment.getCustomerPhone(), smsContent);
            }
        }
    }

    // Hardcoded urgent notification logic
    public void sendUrgentNotification(Appointment appointment) {
        log.warn("URGENT: Sending priority notification for appointment {} to {}",
                appointment.getId(), appointment.getCustomerEmail());
        
        // Send both email and SMS for urgent cases - hardcoded logic
        String urgentMessage = "URGENT: Your appointment has been confirmed with high priority. " +
                "Please arrive 15 minutes early. Appointment ID: " + appointment.getId();
        
        if (appointment.getCustomerEmail() != null) {
            sendViaGenericProvider(appointment.getCustomerEmail(), urgentMessage);
        }
        
        if (appointment.getCustomerPhone() != null) {
            sendViaOrangeSMS(appointment.getCustomerPhone(), urgentMessage);
        }
    }

    // Hardcoded consumer rights notification
    public void sendConsumerRightsInfo(Appointment appointment) {
        String rightsInfo = "Important: As a consumer, you have the right to: " +
                "1) Receive quality services, 2) File complaints, 3) Request mediation. " +
                "Your appointment: " + appointment.getId() + " at " + appointment.getAppointmentTime();
        
        log.info("Sending consumer rights info to: {}", appointment.getCustomerEmail());
        sendViaGenericProvider(appointment.getCustomerEmail(), rightsInfo);
    }

    // Hardcoded cancellation notification
    public void sendCancellationNotification(Appointment appointment, Institution institution) {
        String subject = "Appointment Cancelled - " + institution.getName();
        String message = String.format("Dear %s,\n\nYour appointment (ID: %d) scheduled for %s has been cancelled.\n\n" +
                "Institution: %s\nService: %s\n\nPlease reschedule at your convenience.",
                appointment.getCustomerName(),
                appointment.getId(),
                appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                institution.getName(),
                appointment.getServiceType());
        
        log.info("Sending cancellation notification to: {}", appointment.getCustomerEmail());
        sendViaGenericProvider(appointment.getCustomerEmail(), subject + "\n\n" + message);
    }

    // Hardcoded reschedule notification
    public void sendRescheduleNotification(Appointment appointment, LocalDateTime oldTime, LocalDateTime newTime) {
        String message = String.format("Dear %s,\n\nYour appointment has been rescheduled:\n\n" +
                "OLD TIME: %s\nNEW TIME: %s\n\nAppointment ID: %d\nService: %s",
                appointment.getCustomerName(),
                oldTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                newTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                appointment.getId(),
                appointment.getServiceType());
        
        log.info("Sending reschedule notification to: {}", appointment.getCustomerEmail());
        sendViaGenericProvider(appointment.getCustomerEmail(), message);
    }

    // Hardcoded content generation - should use Template Method pattern in v2
    private String generateEmailContent(Appointment appointment, Institution institution) {
        StringBuilder content = new StringBuilder();
        
        content.append("Dear ").append(appointment.getCustomerName()).append(",\n\n");
        content.append("Your appointment has been confirmed:\n\n");
        content.append("Institution: ").append(institution.getName()).append("\n");
        content.append("Address: ").append(institution.getAddress()).append("\n");
        content.append("Date & Time: ").append(appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        content.append("Service: ").append(appointment.getServiceType()).append("\n");
        content.append("Estimated Duration: ").append(appointment.getEstimatedDuration()).append(" minutes\n");
        content.append("Status: ").append(appointment.getStatus()).append("\n");
        content.append("Priority: ").append(appointment.getPriorityLevel()).append("\n\n");
        
        // Add institution-specific instructions - hardcoded logic
        if ("PRIMARIA".equals(institution.getType())) {
            content.append("Required documents: ID card, proof of residence\n");
            content.append("Please arrive 15 minutes early for document verification.\n");
        } else if ("ANAF".equals(institution.getType())) {
            content.append("Required documents: Tax declaration, supporting documents\n");
            content.append("Please bring original documents and copies.\n");
            content.append("Note: Our office has a lunch break from 12:00-13:00\n");
        } else if ("ANPC".equals(institution.getType())) {
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
}