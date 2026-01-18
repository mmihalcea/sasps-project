package edu.saspsproject.decorator;

import edu.saspsproject.adapter.EmailProvider;
import lombok.extern.slf4j.Slf4j;

/**
 * DECORATOR PATTERN - Retry Email Decorator
 * 
 * Adaugă funcționalitate de retry la trimiterea email-urilor.
 * În caz de eșec, reîncearcă trimiterea de un număr configurabil de ori.
 * 
 * Caracteristici:
 * - Retry configurabil (număr de încercări)
 * - Delay exponențial între încercări
 * - Logging detaliat al fiecărei încercări
 * 
 * Utilizare:
 * <pre>
 * EmailProvider provider = new RetryEmailDecorator(
 *     new GenericEmailAdapter(mailSender),
 *     3,  // maxRetries
 *     1000 // initialDelayMs
 * );
 * </pre>
 */
@Slf4j
public class RetryEmailDecorator extends EmailProviderDecorator {
    
    private final int maxRetries;
    private final long initialDelayMs;
    
    /**
     * Creează un decorator cu retry folosind valorile implicite.
     * 
     * @param wrapped provider-ul de decorat
     */
    public RetryEmailDecorator(EmailProvider wrapped) {
        this(wrapped, 3, 1000);
    }
    
    /**
     * Creează un decorator cu retry cu valori personalizate.
     * 
     * @param wrapped provider-ul de decorat
     * @param maxRetries numărul maxim de încercări (include prima încercare)
     * @param initialDelayMs delay-ul inițial între încercări (va crește exponențial)
     */
    public RetryEmailDecorator(EmailProvider wrapped, int maxRetries, long initialDelayMs) {
        super(wrapped);
        this.maxRetries = maxRetries;
        this.initialDelayMs = initialDelayMs;
    }
    
    @Override
    public boolean sendEmail(String to, String subject, String content) {
        int attempt = 0;
        long currentDelay = initialDelayMs;
        Exception lastException = null;
        
        while (attempt < maxRetries) {
            attempt++;
            
            try {
                log.debug("📧 Încercare {} din {} pentru email către {}", attempt, maxRetries, to);
                
                boolean result = super.sendEmail(to, subject, content);
                
                if (result) {
                    if (attempt > 1) {
                        log.info("✅ Email trimis cu succes la încercarea {} din {}", attempt, maxRetries);
                    }
                    return true;
                } else {
                    log.warn("⚠️ Trimitere email returnează false la încercarea {}", attempt);
                }
            } catch (Exception e) {
                lastException = e;
                log.warn("❌ Încercare {} eșuată: {}", attempt, e.getMessage());
            }
            
            // Dacă nu am reușit și mai avem încercări, așteptăm
            if (attempt < maxRetries) {
                try {
                    log.debug("⏳ Așteptare {}ms înainte de reîncercare...", currentDelay);
                    Thread.sleep(currentDelay);
                    currentDelay *= 2; // Exponential backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.error("Retry întrerupt");
                    break;
                }
            }
        }
        
        log.error("❌ Email EȘUAT după {} încercări către {}", maxRetries, to);
        if (lastException != null) {
            log.error("Ultima eroare: {}", lastException.getMessage());
        }
        
        return false;
    }
    
    @Override
    public String getProviderName() {
        return "Retry[" + wrapped.getProviderName() + "]";
    }
}
