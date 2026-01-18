package edu.saspsproject.template;

import edu.saspsproject.model.Appointment;
import edu.saspsproject.model.User;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/**
 * TEMPLATE METHOD PATTERN - Appointment Cancellation Email Template
 * 
 * Implementare concretă pentru email-uri de anulare.
 * Utilizează culoare roșie pentru a semnala acțiunea negativă.
 */
@Component
public class AppointmentCancellationEmailTemplate extends AbstractEmailTemplate<AppointmentCancellationData> {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    @Override
    protected String generateSubject(AppointmentCancellationData data) {
        return "❌ Programare anulată - " + data.institutionName() + " | SASPS";
    }
    
    @Override
    protected String generateBodyContent(User user, AppointmentCancellationData data) {
        Appointment appointment = data.appointment();
        String reason = data.reason() != null ? data.reason() : "Nu a fost specificat un motiv";
        
        return """
                <div class="header">
                    <h1>❌ Programare Anulată</h1>
                </div>
                <div class="content">
                    <p>Bună ziua, <strong>%s</strong>,</p>
                    <p>Vă informăm că programarea dumneavoastră a fost anulată.</p>
                    
                    <div class="details">
                        <h3>📋 Detalii programare anulată</h3>
                        <p><strong>📍 Instituție:</strong> %s</p>
                        <p><strong>📅 Data și ora programată:</strong> %s</p>
                        <p><strong>🔧 Serviciu:</strong> %s</p>
                        <p><strong>🆔 ID Programare:</strong> #%d</p>
                        <p><strong>📝 Motiv anulare:</strong> %s</p>
                    </div>
                    
                    <p>Puteți face o nouă programare accesând platforma SASPS oricând.</p>
                    
                    <p>Ne cerem scuze pentru eventualele inconveniente.</p>
                </div>
                """.formatted(
                user.getName(),
                data.institutionName(),
                appointment.getAppointmentTime().format(DATE_FORMATTER),
                formatServiceType(appointment.getServiceType()),
                appointment.getId(),
                reason
        );
    }
    
    @Override
    protected String getHeaderColor() {
        return "#f44336"; // Roșu pentru anulare/eroare
    }
    
    @Override
    protected String getFooterText() {
        return "Dacă aveți întrebări, vă rugăm să ne contactați la support@sasps.ro";
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
