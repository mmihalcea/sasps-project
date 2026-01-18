package edu.saspsproject.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * FACTORY PATTERN + ADAPTER PATTERN - SMS Provider Factory
 * 
 * Selectează automat provider-ul SMS potrivit în funcție de numărul de telefon.
 * Utilizează Chain of Responsibility implicit prin verificarea supports().
 * 
 * Beneficii:
 * - Abstractizează selecția provider-ului
 * - Permite fallback automat la alt provider
 * - Suportă load balancing în producție
 * - Extensibil pentru noi operatori
 * 
 * Utilizare:
 * <pre>
 * SmsProvider provider = smsProviderFactory.getProvider("0721234567");
 * provider.sendSms(phoneNumber, message);
 * </pre>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SmsProviderFactory {
    
    private final List<SmsProvider> smsProviders;
    private final TelekomSmsAdapter fallbackProvider;
    
    /**
     * Returnează provider-ul SMS potrivit pentru numărul de telefon.
     * Utilizează metoda supports() pentru a determina compatibilitatea.
     * 
     * @param phoneNumber numărul de telefon în format 07xxxxxxxx
     * @return provider-ul SMS potrivit sau fallback-ul Telekom
     */
    public SmsProvider getProvider(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            log.warn("Număr de telefon invalid, se utilizează provider-ul fallback");
            return fallbackProvider;
        }
        
        // Încearcă să găsească un provider care suportă numărul
        for (SmsProvider provider : smsProviders) {
            if (provider.supports(phoneNumber)) {
                log.debug("Provider selectat pentru {}: {}", phoneNumber, provider.getProviderName());
                return provider;
            }
        }
        
        // Fallback la Telekom pentru numere nerecunoscute
        log.warn("Nu s-a găsit provider pentru {}, se utilizează Telekom ca fallback", phoneNumber);
        return fallbackProvider;
    }
    
    /**
     * Trimite SMS folosind provider-ul potrivit automat.
     * Metodă convenience care combină selecția provider-ului cu trimiterea.
     * 
     * @param phoneNumber numărul de telefon destinatar
     * @param message mesajul SMS
     * @return true dacă SMS-ul a fost trimis cu succes
     */
    public boolean sendSms(String phoneNumber, String message) {
        SmsProvider provider = getProvider(phoneNumber);
        return provider.sendSms(phoneNumber, message);
    }
}
