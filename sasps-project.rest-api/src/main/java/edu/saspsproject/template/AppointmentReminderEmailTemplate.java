package edu.saspsproject.template;

import edu.saspsproject.model.Appointment;
import edu.saspsproject.model.User;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/**
 * TEMPLATE METHOD PATTERN - Appointment Reminder Email Template
 * 
 * Implementare concretă pentru email-uri de reminder.
 * Utilizează culoare portocalie pentru a atrage atenția.
 */
@Component
public class AppointmentReminderEmailTemplate extends AbstractEmailTemplate<AppointmentEmailData> {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    @Override
    protected String generateSubject(AppointmentEmailData data) {
        return "🔔 Reminder: Programare mâine - " + data.institutionName() + " | SASPS";
    }
    
    @Override
    protected String generateBodyContent(User user, AppointmentEmailData data) {
        Appointment appointment = data.appointment();
        
        return """
                <div class="header">
                    <h1>🔔 Reminder Programare</h1>
                </div>
                <div class="content">
                    <p>Bună ziua, <strong>%s</strong>!</p>
                    
                    <div class="warning">
                        <strong>⚠️ Atenție!</strong> Aveți o programare mâine!
                    </div>
                    
                    <div class="details">
                        <h3>📋 Detalii programare</h3>
                        <p><strong>📍 Instituție:</strong> %s</p>
                        <p><strong>📅 Data și ora:</strong> %s</p>
                        <p><strong>🔧 Serviciu:</strong> %s</p>
                        <p><strong>🆔 ID Programare:</strong> #%d</p>
                    </div>
                    
                    <p><strong>📄 Documente necesare:</strong></p>
                    <ul>
                        <li>Carte de identitate (original + copie)</li>
                        <li>%s</li>
                    </ul>
                    
                    <p><strong>⏰ Vă rugăm să ajungeți cu 10 minute înainte!</strong></p>
                </div>
                """.formatted(
                user.getName(),
                data.institutionName(),
                appointment.getAppointmentTime().format(DATE_FORMATTER),
                formatServiceType(appointment.getServiceType()),
                appointment.getId(),
                appointment.getDocumentRequired() != null ? appointment.getDocumentRequired() : "Documente specifice serviciului"
        );
    }
    
    @Override
    protected String getHeaderColor() {
        return "#FF9800"; // Portocaliu pentru avertizare/reminder
    }
    
    @Override
    protected String getFooterText() {
        return "Acest reminder a fost trimis automat. Nu mai puteți anula programarea cu mai puțin de 24 de ore înainte.";
    }
    
    private String formatServiceType(Appointment.ServiceType serviceType) {
        if (serviceType == null) return "Nedefinit";
        return switch (serviceType) {
            case ELIBERARE_CI -> "Eliberare carte de identitate";
            case CERTIFICAT_NASTERE -> "Eliberare certificat de naștere";
            case DECLARATIE_FISCALA -> "Depunere declarație fiscală";
            case PRESCHIMBARE_PERMIS -> "Preschimbare permis de conducere";
            case INMATRICULARE_VEHICUL -> "Înmatriculare vehicul";
        };
    }
}
