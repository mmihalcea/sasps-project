package edu.saspsproject.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * ADAPTER PATTERN - Telekom SMS Provider Adapter
 * 
 * Adaptează API-ul Telekom România la interfața comună SmsProvider.
 * Funcționează ca fallback pentru numere care nu sunt gestionate de Orange/Vodafone.
 * 
 * Caracteristici specifice Telekom:
 * - API SOAP legacy
 * - Suport pentru SMS-uri premium
 * - Compatibilitate cu numerele fixe (pentru voce)
 */
@Slf4j
@Component
public class TelekomSmsAdapter implements SmsProvider {
    
    private static final String[] SUPPORTED_PREFIXES = {"076", "077"};
    
    @Override
    public boolean sendSms(String phoneNumber, String message) {
        if (!supports(phoneNumber)) {
            log.warn("[TELEKOM-SMS] Numărul {} nu este suportat de Telekom", phoneNumber);
            return false;
        }
        
        log.info("[TELEKOM-SMS] Trimitere SMS către: {}", phoneNumber);
        log.debug("[TELEKOM-SMS] Protocol: SOAP, lungime mesaj: {} caractere", message.length());
        
        try {
            // Simulare apel API Telekom
            simulateTelekomApi(phoneNumber, message);
            log.info("[TELEKOM-SMS] SMS trimis cu succes către {}", phoneNumber);
            return true;
        } catch (Exception e) {
            log.error("[TELEKOM-SMS] Eroare la trimiterea SMS-ului: {}", e.getMessage());
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
        // Telekom este fallback pentru numere nerecunoscute
        return phoneNumber.startsWith("07");
    }
    
    @Override
    public String getProviderName() {
        return "Telekom România SMS Gateway";
    }
    
    private void simulateTelekomApi(String phoneNumber, String message) {
        log.debug("[TELEKOM-SMS] Construire SOAP envelope...");
        log.debug("[TELEKOM-SMS] Apel serviciu web sendSMS...");
        log.debug("[TELEKOM-SMS] Response SOAP: success, id: {}", System.currentTimeMillis());
    }
}
