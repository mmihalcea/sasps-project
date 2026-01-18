package edu.saspsproject.decorator;

import edu.saspsproject.adapter.EmailProvider;
import lombok.extern.slf4j.Slf4j;

/**
 * DECORATOR PATTERN - Email Provider Interface (pentru decoratori)
 * 
 * Această clasă de bază permite decorarea EmailProvider-urilor
 * cu funcționalități adiționale fără a modifica implementările originale.
 * 
 * Decorator Pattern permite:
 * - Adăugarea de comportament la runtime
 * - Combinarea mai multor decoratori
 * - Respectarea Open/Closed Principle
 * - Evitarea explosion-ului de subclase
 * 
 * Exemplu de utilizare:
 * <pre>
 * EmailProvider provider = new LoggingEmailDecorator(
 *     new MetricsEmailDecorator(
 *         new GenericEmailAdapter(mailSender)
 *     )
 * );
 * </pre>
 */
@Slf4j
public abstract class EmailProviderDecorator implements EmailProvider {
    
    protected final EmailProvider wrapped;
    
    /**
     * Construiește un decorator care învelește alt provider.
     * 
     * @param wrapped provider-ul de decorat
     */
    protected EmailProviderDecorator(EmailProvider wrapped) {
        if (wrapped == null) {
            throw new IllegalArgumentException("Wrapped provider cannot be null");
        }
        this.wrapped = wrapped;
    }
    
    @Override
    public boolean sendEmail(String to, String subject, String content) {
        return wrapped.sendEmail(to, subject, content);
    }
    
    @Override
    public String getProviderName() {
        return wrapped.getProviderName();
    }
}
