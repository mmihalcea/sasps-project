package edu.saspsproject.template;

import edu.saspsproject.model.Appointment;
import edu.saspsproject.model.User;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/**
 * TEMPLATE METHOD PATTERN - Appointment Confirmation Email Template
 * 
 * Implementare concretă a template-ului de email pentru confirmări de programări.
 * Extinde AbstractEmailTemplate și implementează metodele abstracte specifice.
 * 
 * Responsabilități:
 * - Generare subiect pentru email de confirmare
 * - Generare conținut HTML specific confirmării
 * - Utilizare culoare verde pentru header (succes)
 */
@Component
public class AppointmentConfirmationEmailTemplate extends AbstractEmailTemplate<AppointmentEmailData> {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    @Override
    protected String generateSubject(AppointmentEmailData data) {
        return "✓ Confirmare programare - " + data.institutionName() + " | SASPS";
    }
    
    @Override
    protected String generateBodyContent(User user, AppointmentEmailData data) {
        Appointment appointment = data.appointment();
        
        return """
                <div class="header">
                    <h1>✓ Programare Confirmată</h1>
                </div>
                <div class="content">
                    <p>Bună ziua, <strong>%s</strong>!</p>
                    <p>Programarea dumneavoastră a fost confirmată cu succes.</p>
                    
                    <div class="details">
                        <h3>📋 Detalii programare</h3>
                        <p><strong>📍 Instituție:</strong> %s</p>
                        <p><strong>📅 Data și ora:</strong> %s</p>
                        <p><strong>🔧 Serviciu:</strong> %s</p>
                        <p><strong>⏱️ Durată estimată:</strong> %.0f minute</p>
                        <p><strong>📊 Prioritate:</strong> %s</p>
                        <p><strong>🆔 ID Programare:</strong> #%d</p>
                    </div>
                    
                    <div class="warning">
                        <strong>⚠️ Important:</strong> Vă rugăm să ajungeți cu 10 minute înainte de ora programării.
                    </div>
                    
                    <p><strong>📄 Documente necesare:</strong> %s</p>
                    
                    <p>Veți primi un reminder cu 24 de ore înainte de programare.</p>
                </div>
                """.formatted(
                user.getName(),
                data.institutionName(),
                appointment.getAppointmentTime().format(DATE_FORMATTER),
                formatServiceType(appointment.getServiceType()),
                appointment.getEstimatedDuration(),
                formatPriority(appointment.getPriorityLevel()),
                appointment.getId(),
                appointment.getDocumentRequired() != null ? appointment.getDocumentRequired() : "Carte de identitate"
        );
    }
    
    @Override
    protected String getHeaderColor() {
        return "#4CAF50"; // Verde pentru succes/confirmare
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
    
    private String formatPriority(Appointment.PriorityLevel priority) {
        if (priority == null) return "Normal";
        return switch (priority) {
            case LOW -> "Scăzută";
            case MEDIUM -> "Normală";
            case HIGH -> "Ridicată";
            case URGENT -> "⚡ Urgentă";
        };
    }
}
