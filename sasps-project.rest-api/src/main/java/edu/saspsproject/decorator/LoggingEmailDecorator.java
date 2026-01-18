package edu.saspsproject.decorator;

import edu.saspsproject.adapter.EmailProvider;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DECORATOR PATTERN - Logging Email Decorator
 * 
 * Adaugă logging detaliat la operațiunile de email fără a modifica
 * implementările originale ale provider-ilor.
 * 
 * Loggează:
 * - Timestamp-ul trimiterii
 * - Destinatarul și subiectul
 * - Durata operațiunii
 * - Rezultatul (succes/eșec)
 * 
 * Poate fi combinat cu alte decoratoare:
 * <pre>
 * EmailProvider provider = new LoggingEmailDecorator(
 *     new RetryEmailDecorator(
 *         new GenericEmailAdapter(mailSender)
 *     )
 * );
 * </pre>
 */
@Slf4j
public class LoggingEmailDecorator extends EmailProviderDecorator {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    public LoggingEmailDecorator(EmailProvider wrapped) {
        super(wrapped);
    }
    
    @Override
    public boolean sendEmail(String to, String subject, String content) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        long startTime = System.currentTimeMillis();
        
        log.info("═══════════════════════════════════════════════════════════");
        log.info("📧 EMAIL OPERATION START");
        log.info("═══════════════════════════════════════════════════════════");
        log.info("⏰ Timestamp: {}", timestamp);
        log.info("📬 Provider: {}", wrapped.getProviderName());
        log.info("📨 To: {}", maskEmail(to));
        log.info("📋 Subject: {}", subject);
        log.info("📏 Content length: {} characters", content != null ? content.length() : 0);
        log.info("───────────────────────────────────────────────────────────");
        
        boolean result;
        try {
            result = super.sendEmail(to, subject, content);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("❌ EMAIL FAILED after {}ms", duration);
            log.error("🔴 Error: {}", e.getMessage());
            log.info("═══════════════════════════════════════════════════════════\n");
            throw e;
        }
        
        long duration = System.currentTimeMillis() - startTime;
        
        if (result) {
            log.info("✅ EMAIL SENT SUCCESSFULLY");
        } else {
            log.warn("⚠️ EMAIL SENDING RETURNED FALSE");
        }
        log.info("⏱️ Duration: {}ms", duration);
        log.info("═══════════════════════════════════════════════════════════\n");
        
        return result;
    }
    
    @Override
    public String getProviderName() {
        return "Logging[" + wrapped.getProviderName() + "]";
    }
    
    /**
     * Maschează parțial adresa de email pentru GDPR compliance în log-uri.
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        
        String[] parts = email.split("@");
        String local = parts[0];
        String domain = parts[1];
        
        if (local.length() <= 2) {
            return "**@" + domain;
        }
        
        return local.charAt(0) + "***" + local.charAt(local.length() - 1) + "@" + domain;
    }
}
