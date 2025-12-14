package edu.saspsproject.service;

import edu.saspsproject.model.Appointment;
import edu.saspsproject.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * Email service without design patterns, intentionally duplicating code
 * for baseline comparison with pattern-based implementation
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Duplicated code, no template method pattern
    public void sendAppointmentConfirmationEmail(User user, Appointment appointment, String institutionName) {
        if (user.getEmail() == null || !user.getEmailNotificationsEnabled()) {
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("Confirmare programare, SASPS");

            // Hardcoded HTML, no template engine usage
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String htmlContent = "<!DOCTYPE html><html><head><style>"
                    + "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }"
                    + ".container { max-width: 600px; margin: 0 auto; padding: 20px; }"
                    + ".header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }"
                    + ".content { padding: 20px; background-color: #f9f9f9; }"
                    + ".details { background-color: white; padding: 15px; margin: 10px 0; border-left: 4px solid #4CAF50; }"
                    + ".footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }"
                    + "</style></head><body><div class='container'>"
                    + "<div class='header'><h1>Programare confirmată</h1></div>"
                    + "<div class='content'>"
                    + "<p>Bună " + user.getName() + ",</p>"
                    + "<p>Programarea dumneavoastră a fost confirmată cu succes!</p>"
                    + "<div class='details'>"
                    + "<h3>Detalii programare:</h3>"
                    + "<p><strong>Instituție:</strong> " + institutionName + "</p>"
                    + "<p><strong>Data și ora:</strong> " + appointment.getAppointmentTime().format(formatter) + "</p>"
                    + "<p><strong>Serviciu:</strong> " + appointment.getServiceType() + "</p>"
                    + "<p><strong>Prioritate:</strong> " + appointment.getPriorityLevel() + "</p>"
                    + "</div>"
                    + "<p>Vă rugăm să ajungeți cu 10 minute înainte de ora programării.</p>"
                    + "<p>Veți primi un reminder cu 24 de ore înainte de programare.</p>"
                    + "</div>"
                    + "<div class='footer'>"
                    + "<p>Acest email a fost generat automat. Vă rugăm să nu răspundeți.</p>"
                    + "<p>&copy; 2025 SASPS, Sistem de Agendat la Servicii Publice</p>"
                    + "</div></div></body></html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
            System.out.println("Confirmation email sent to: " + user.getEmail());
        } catch (MessagingException e) {
            System.err.println("Failed to send confirmation email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // More duplicated code, no strategy pattern
    public void sendAppointmentReminderEmail(User user, Appointment appointment, String institutionName) {
        if (user.getEmail() == null || !user.getEmailNotificationsEnabled()) {
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("Reminder: Programare mâine, SASPS");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String htmlContent = "<!DOCTYPE html><html><head><style>"
                    + "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }"
                    + ".container { max-width: 600px; margin: 0 auto; padding: 20px; }"
                    + ".header { background-color: #FF9800; color: white; padding: 20px; text-align: center; }"
                    + ".content { padding: 20px; background-color: #f9f9f9; }"
                    + ".details { background-color: white; padding: 15px; margin: 10px 0; border-left: 4px solid #FF9800; }"
                    + ".warning { background-color: #fff3cd; padding: 10px; border-radius: 5px; margin: 10px 0; }"
                    + ".footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }"
                    + "</style></head><body><div class='container'>"
                    + "<div class='header'><h1>🔔 Reminder Programare</h1></div>"
                    + "<div class='content'>"
                    + "<p>Bună " + user.getName() + ",</p>"
                    + "<div class='warning'>"
                    + "<p><strong>Atenție!</strong> Aveți o programare mâine!</p>"
                    + "</div>"
                    + "<div class='details'>"
                    + "<h3>Detalii programare:</h3>"
                    + "<p><strong>Instituție:</strong> " + institutionName + "</p>"
                    + "<p><strong>Data și ora:</strong> " + appointment.getAppointmentTime().format(formatter) + "</p>"
                    + "<p><strong>Serviciu:</strong> " + appointment.getServiceType() + "</p>"
                    + "</div>"
                    + "<p><strong>Documente necesare:</strong></p>"
                    + "<ul>"
                    + "<li>Carte de identitate</li>"
                    + "<li>Documente specifice serviciului solicitat</li>"
                    + "</ul>"
                    + "<p>Vă rugăm să ajungeți cu 10 minute înainte!</p>"
                    + "</div>"
                    + "<div class='footer'>"
                    + "<p>Acest email a fost generat automat. Vă rugăm să nu răspundeți.</p>"
                    + "<p>&copy; 2025 SASPS</p>"
                    + "</div></div></body></html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
            System.out.println("Reminder email sent to: " + user.getEmail());
        } catch (MessagingException e) {
            System.err.println("Failed to send reminder email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Even more duplication, no builder pattern
    public void sendAppointmentCancellationEmail(User user, Appointment appointment, String institutionName, String reason) {
        if (user.getEmail() == null || !user.getEmailNotificationsEnabled()) {
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("Programare anulată, SASPS");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String htmlContent = "<!DOCTYPE html><html><head><style>"
                    + "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }"
                    + ".container { max-width: 600px; margin: 0 auto; padding: 20px; }"
                    + ".header { background-color: #f44336; color: white; padding: 20px; text-align: center; }"
                    + ".content { padding: 20px; background-color: #f9f9f9; }"
                    + ".details { background-color: white; padding: 15px; margin: 10px 0; border-left: 4px solid #f44336; }"
                    + ".footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }"
                    + "</style></head><body><div class='container'>"
                    + "<div class='header'><h1>Programare anulată</h1></div>"
                    + "<div class='content'>"
                    + "<p>Bună " + user.getName() + ",</p>"
                    + "<p>Programarea dumneavoastră a fost anulată.</p>"
                    + "<div class='details'>"
                    + "<h3>Detalii programare anulată:</h3>"
                    + "<p><strong>Instituție:</strong> " + institutionName + "</p>"
                    + "<p><strong>Data și ora:</strong> " + appointment.getAppointmentTime().format(formatter) + "</p>"
                    + "<p><strong>Serviciu:</strong> " + appointment.getServiceType() + "</p>"
                    + "<p><strong>Motiv:</strong> " + (reason != null ? reason : "Nu a fost specificat") + "</p>"
                    + "</div>"
                    + "<p>Puteți face o nouă programare accesând platforma SASPS.</p>"
                    + "</div>"
                    + "<div class='footer'>"
                    + "<p>Acest email a fost generat automat. Vă rugăm să nu răspundeți.</p>"
                    + "<p>&copy; 2025 SASPS</p>"
                    + "</div></div></body></html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
            System.out.println("Cancellation email sent to: " + user.getEmail());
        } catch (MessagingException e) {
            System.err.println("Failed to send cancellation email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Welcome email, more duplication
    public void sendWelcomeEmail(User user) {
        if (user.getEmail() == null) {
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("Bun venit la SASPS!");

            String htmlContent = "<!DOCTYPE html><html><head><style>"
                    + "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }"
                    + ".container { max-width: 600px; margin: 0 auto; padding: 20px; }"
                    + ".header { background-color: #2196F3; color: white; padding: 20px; text-align: center; }"
                    + ".content { padding: 20px; background-color: #f9f9f9; }"
                    + ".features { background-color: white; padding: 15px; margin: 10px 0; }"
                    + ".footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }"
                    + "</style></head><body><div class='container'>"
                    + "<div class='header'><h1>Bun venit!</h1></div>"
                    + "<div class='content'>"
                    + "<p>Bună " + user.getName() + ",</p>"
                    + "<p>Bine ați venit la SASPS, Sistemul de Agendat la Servicii Publice!</p>"
                    + "<div class='features'>"
                    + "<h3>Ce puteți face cu SASPS:</h3>"
                    + "<ul>"
                    + "<li>Programări online la instituțiile publice</li>"
                    + "<li>Notificări automate prin email</li>"
                    + "<li>Remindere înaintea programărilor</li>"
                    + "<li>Gestionarea programărilor active</li>"
                    + "</ul>"
                    + "</div>"
                    + "<p>Vă mulțumim că ați ales SASPS!</p>"
                    + "</div>"
                    + "<div class='footer'>"
                    + "<p>&copy; 2025 SASPS</p>"
                    + "</div></div></body></html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
            System.out.println("Welcome email sent to: " + user.getEmail());
        } catch (MessagingException e) {
            System.err.println("Failed to send welcome email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
