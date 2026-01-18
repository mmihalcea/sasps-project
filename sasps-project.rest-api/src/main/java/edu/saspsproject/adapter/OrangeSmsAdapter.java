package edu.saspsproject.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * ADAPTER PATTERN - Orange SMS Provider Adapter
 * 
 * Adaptează API-ul Orange România la interfața comună SmsProvider.
 * Gestionează numerele care încep cu prefixul 07[2-4].
 * 
 * Caracteristici specifice Orange:
 * - API REST pentru trimitere SMS-uri
 * - Suport pentru SMS-uri concatenate (lungime > 160)
 * - Delivery reports
 * - Rate limiting conform contractului
 */
@Slf4j
@Component
public class OrangeSmsAdapter implements SmsProvider {
    
    private static final String[] SUPPORTED_PREFIXES = {"072", "073", "074"};
    
    @Override
    public boolean sendSms(String phoneNumber, String message) {
        if (!supports(phoneNumber)) {
            log.warn("[ORANGE-SMS] Numărul {} nu este suportat de Orange", phoneNumber);
            return false;
        }
        
        log.info("[ORANGE-SMS] Trimitere SMS către: {}", phoneNumber);
        log.debug("[ORANGE-SMS] Lungime mesaj: {} caractere", message.length());
        
        try {
            // Simulare apel API Orange
            simulateOrangeApi(phoneNumber, message);
            log.info("[ORANGE-SMS] SMS trimis cu succes către {}", phoneNumber);
            return true;
        } catch (Exception e) {
            log.error("[ORANGE-SMS] Eroare la trimiterea SMS-ului: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean supports(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 3) {
            return false;
        }
        for (String prefix : SUPPORTED_PREFIXES) {
            if (phoneNumber.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String getProviderName() {
        return "Orange România SMS Gateway";
    }
    
    private void simulateOrangeApi(String phoneNumber, String message) {
        log.debug("[ORANGE-SMS] Conectare la Orange SMS Gateway...");
        log.debug("[ORANGE-SMS] Autentificare API key...");
        log.debug("[ORANGE-SMS] Trimitere request POST /sms/send");
        log.debug("[ORANGE-SMS] Response: 200 OK, message_id: {}", System.currentTimeMillis());
    }
}
