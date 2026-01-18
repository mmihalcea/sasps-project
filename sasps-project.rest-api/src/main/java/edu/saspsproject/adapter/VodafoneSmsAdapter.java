package edu.saspsproject.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * ADAPTER PATTERN - Vodafone SMS Provider Adapter
 * 
 * Adaptează API-ul Vodafone România la interfața comună SmsProvider.
 * Gestionează numerele care încep cu prefixul 07[0,2,5].
 * 
 * Caracteristici specifice Vodafone:
 * - SMPP protocol pentru high volume
 * - Suport Unicode pentru caractere speciale
 * - Callback URL pentru delivery reports
 */
@Slf4j
@Component
public class VodafoneSmsAdapter implements SmsProvider {
    
    private static final String[] SUPPORTED_PREFIXES = {"070", "072", "075"};
    
    @Override
    public boolean sendSms(String phoneNumber, String message) {
        if (!supports(phoneNumber)) {
            log.warn("[VODAFONE-SMS] Numărul {} nu este suportat de Vodafone", phoneNumber);
            return false;
        }
        
        log.info("[VODAFONE-SMS] Trimitere SMS către: {}", phoneNumber);
        log.debug("[VODAFONE-SMS] Encoding: UTF-8, lungime: {} caractere", message.length());
        
        try {
            // Simulare apel API Vodafone
            simulateVodafoneApi(phoneNumber, message);
            log.info("[VODAFONE-SMS] SMS trimis cu succes către {}", phoneNumber);
            return true;
        } catch (Exception e) {
            log.error("[VODAFONE-SMS] Eroare la trimiterea SMS-ului: {}", e.getMessage());
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
        return "Vodafone România SMS Gateway";
    }
    
    private void simulateVodafoneApi(String phoneNumber, String message) {
        log.debug("[VODAFONE-SMS] Conectare SMPP la Vodafone...");
        log.debug("[VODAFONE-SMS] Bind transceiver...");
        log.debug("[VODAFONE-SMS] Submit SM PDU...");
        log.debug("[VODAFONE-SMS] Response: SUBMIT_SM_RESP, status: ESME_ROK");
    }
}
