package edu.saspsproject.template;

import edu.saspsproject.adapter.EmailProvider;
import edu.saspsproject.model.User;
import lombok.extern.slf4j.Slf4j;

/**
 * TEMPLATE METHOD PATTERN - Abstract Email Template
 * 
 * Definește scheletul algoritmului pentru trimiterea email-urilor,
 * delegând pașii specifici către subclase.
 * 
 * Template Method Pattern este util când:
 * - Există un algoritm cu pași comuni și pași variabili
 * - Vrem să evităm duplicarea codului
 * - Vrem să controlăm punctele de extensibilitate
 * 
 * Algoritm (pași fix):
 * 1. Validare destinatar (metoda comună)
 * 2. Generare subiect (metoda abstractă - implementată de subclase)
 * 3. Generare conținut HTML (metoda abstractă - implementată de subclase)
 * 4. Aplicare stil CSS (metoda comună cu hook)
 * 5. Trimitere email (metoda comună)
 * 
 * Hook methods:
 * - getHeaderColor() - permite personalizarea culorii header-ului
 * - getFooterText() - permite personalizarea footer-ului
 * 
 * @param <T> tipul de date specifice email-ului (Appointment, User, etc.)
 */
@Slf4j
public abstract class AbstractEmailTemplate<T> {
    
    /**
     * Template method - definește algoritmul fix pentru trimiterea email-ului.
     * Această metodă este finală pentru a preveni modificarea structurii algoritmului.
     * 
     * @param user destinatarul email-ului
     * @param data datele specifice pentru generarea conținutului
     * @param emailProvider provider-ul de email care va trimite mesajul
     * @return true dacă email-ul a fost trimis cu succes
     */
    public final boolean sendEmail(User user, T data, EmailProvider emailProvider) {
        // Step 1: Validare destinatar (metodă comună)
        if (!validateRecipient(user)) {
            log.warn("Destinatar invalid sau notificări dezactivate pentru: {}", 
                    user != null ? user.getEmail() : "null");
            return false;
        }
        
        // Step 2: Generare subiect (metodă abstractă)
        String subject = generateSubject(data);
        
        // Step 3: Generare conținut specific (metodă abstractă)
        String bodyContent = generateBodyContent(user, data);
        
        // Step 4: Aplicare template HTML comun
        String fullHtmlContent = applyHtmlTemplate(bodyContent);
        
        // Step 5: Trimitere email
        log.info("Trimitere email '{}' către {}", subject, user.getEmail());
        return emailProvider.sendEmail(user.getEmail(), subject, fullHtmlContent);
    }
    
    /**
     * Validează dacă destinatarul poate primi email-uri.
     * Metodă comună pentru toate tipurile de email.
     */
    protected boolean validateRecipient(User user) {
        return user != null 
                && user.getEmail() != null 
                && !user.getEmail().isBlank()
                && Boolean.TRUE.equals(user.getEmailNotificationsEnabled());
    }
    
    /**
     * Generează subiectul email-ului.
     * Trebuie implementată de subclase.
     */
    protected abstract String generateSubject(T data);
    
    /**
     * Generează conținutul specific al email-ului (body).
     * Trebuie implementată de subclase.
     */
    protected abstract String generateBodyContent(User user, T data);
    
    /**
     * Hook method - returnează culoarea header-ului.
     * Poate fi suprascrisă de subclase pentru personalizare.
     */
    protected String getHeaderColor() {
        return "#4CAF50"; // Verde implicit
    }
    
    /**
     * Hook method - returnează textul footer-ului.
     * Poate fi suprascrisă pentru mesaje personalizate.
     */
    protected String getFooterText() {
        return "Acest email a fost generat automat de sistemul SASPS. Vă rugăm să nu răspundeți.";
    }
    
    /**
     * Aplică template-ul HTML comun pentru toate email-urile.
     * Include stiluri CSS, header și footer consistent.
     */
    private String applyHtmlTemplate(String bodyContent) {
        String headerColor = getHeaderColor();
        String footerText = getFooterText();
        
        return """
                <!DOCTYPE html>
                <html lang="ro">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body { 
                            font-family: 'Segoe UI', Arial, sans-serif; 
                            line-height: 1.6; 
                            color: #333; 
                            margin: 0;
                            padding: 0;
                            background-color: #f4f4f4;
                        }
                        .container { 
                            max-width: 600px; 
                            margin: 20px auto; 
                            background-color: white;
                            border-radius: 8px;
                            overflow: hidden;
                            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                        }
                        .header { 
                            background-color: %s; 
                            color: white; 
                            padding: 30px; 
                            text-align: center; 
                        }
                        .header h1 {
                            margin: 0;
                            font-size: 24px;
                        }
                        .content { 
                            padding: 30px; 
                            background-color: #ffffff; 
                        }
                        .details { 
                            background-color: #f9f9f9; 
                            padding: 20px; 
                            margin: 20px 0; 
                            border-left: 4px solid %s; 
                            border-radius: 0 4px 4px 0;
                        }
                        .details h3 {
                            margin-top: 0;
                            color: #333;
                        }
                        .details p {
                            margin: 8px 0;
                        }
                        .warning {
                            background-color: #fff3cd;
                            padding: 15px;
                            border-radius: 4px;
                            margin: 15px 0;
                            border-left: 4px solid #ffc107;
                        }
                        .footer { 
                            text-align: center; 
                            padding: 20px; 
                            font-size: 12px; 
                            color: #666; 
                            background-color: #f9f9f9;
                            border-top: 1px solid #eee;
                        }
                        .button {
                            display: inline-block;
                            padding: 12px 24px;
                            background-color: %s;
                            color: white;
                            text-decoration: none;
                            border-radius: 4px;
                            margin-top: 15px;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        %s
                        <div class="footer">
                            <p>%s</p>
                            <p>&copy; 2025 SASPS - Sistem de Agendat la Servicii Publice</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(headerColor, headerColor, headerColor, bodyContent, footerText);
    }
}
