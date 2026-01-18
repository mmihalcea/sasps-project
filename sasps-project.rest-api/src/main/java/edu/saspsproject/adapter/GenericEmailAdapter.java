package edu.saspsproject.adapter;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * ADAPTER PATTERN - Generic Email Provider Adapter
 * 
 * Adaptează JavaMailSender la interfața comună EmailProvider.
 * Folosit ca provider implicit pentru instituții care nu au cerințe speciale.
 * 
 * Caracteristici:
 * - Utilizează Spring Mail pentru trimiterea email-urilor
 * - Suport HTML și text plain
 * - Configurabil prin application.properties
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GenericEmailAdapter implements EmailProvider {
    
    private final JavaMailSender mailSender;
    
    @Value("${app.mail.from:noreply@sasps.ro}")
    private String fromEmail;
    
    @Override
    public boolean sendEmail(String to, String subject, String content) {
        log.info("[GENERIC] Trimitere email standard către: {}", to);
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // true = HTML content
            
            mailSender.send(message);
            log.info("[GENERIC] Email trimis cu succes către {}", to);
            return true;
        } catch (MessagingException e) {
            log.error("[GENERIC] Eroare la trimiterea email-ului: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public String getProviderName() {
        return "Generic SMTP Email Service";
    }
}
