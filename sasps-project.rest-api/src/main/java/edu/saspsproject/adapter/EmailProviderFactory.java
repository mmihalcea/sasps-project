package edu.saspsproject.adapter;

import edu.saspsproject.model.Institution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * FACTORY PATTERN + ADAPTER PATTERN - Email Provider Factory
 * 
 * Combină Factory Pattern cu Adapter Pattern pentru a returna
 * provider-ul de email potrivit în funcție de tipul instituției.
 * 
 * Beneficii:
 * - Centralizează logica de selecție a provider-ului
 * - Elimină if/else statements din codul client
 * - Permite extinderea ușoară cu noi tipuri de instituții
 * - Respectă Single Responsibility Principle (SRP)
 * 
 * Utilizare:
 * <pre>
 * EmailProvider provider = emailProviderFactory.getProvider(institution.getType());
 * provider.sendEmail(to, subject, content);
 * </pre>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailProviderFactory {
    
    private final GovEmailAdapter govEmailAdapter;
    private final LocalGovEmailAdapter localGovEmailAdapter;
    private final GenericEmailAdapter genericEmailAdapter;
    
    /**
     * Returnează provider-ul de email potrivit pentru tipul de instituție.
     * 
     * @param institutionType tipul instituției
     * @return provider-ul de email corespunzător
     */
    public EmailProvider getProvider(Institution.InstitutionType institutionType) {
        if (institutionType == null) {
            log.warn("Tip instituție null, se utilizează provider-ul generic");
            return genericEmailAdapter;
        }
        
        EmailProvider provider = switch (institutionType) {
            case ANAF -> govEmailAdapter;
            case PRIMARIA -> localGovEmailAdapter;
            case ANPC, POLITIA_LOCALA, DRPCIV, SPCLEP -> genericEmailAdapter;
        };
        
        log.debug("Provider selectat pentru {}: {}", institutionType, provider.getProviderName());
        return provider;
    }
}
