package edu.saspsproject.strategy;

import edu.saspsproject.model.Appointment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * STRATEGY PATTERN - Priority Strategy Context
 * 
 * Context care gestionează strategiile de prioritizare și selectează
 * strategia potrivită în funcție de caracteristicile programării.
 * 
 * Utilizare:
 * <pre>
 * Appointment.PriorityLevel priority = priorityContext.calculatePriority(appointment);
 * double duration = priorityContext.calculateEstimatedDuration(appointment);
 * </pre>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PriorityStrategyContext {
    
    private final List<PriorityStrategy> strategies;
    
    /**
     * Calculează prioritatea folosind prima strategie aplicabilă.
     * Strategiile sunt sortate după ordine.
     * 
     * @param appointment programarea pentru care se calculează prioritatea
     * @return nivelul de prioritate calculat
     */
    public Appointment.PriorityLevel calculatePriority(Appointment appointment) {
        return findApplicableStrategy(appointment)
                .calculatePriority(appointment);
    }
    
    /**
     * Calculează durata estimată folosind strategia aplicabilă.
     * 
     * @param appointment programarea pentru care se calculează durata
     * @return durata estimată în minute
     */
    public double calculateEstimatedDuration(Appointment appointment) {
        return findApplicableStrategy(appointment)
                .calculateEstimatedDuration(appointment);
    }
    
    /**
     * Găsește prima strategie aplicabilă, sortată după ordine.
     * Dacă nicio strategie nu se aplică, folosește StandardServicePriorityStrategy.
     */
    private PriorityStrategy findApplicableStrategy(Appointment appointment) {
        return strategies.stream()
                .sorted(Comparator.comparingInt(PriorityStrategy::getOrder))
                .filter(strategy -> strategy.appliesTo(appointment))
                .findFirst()
                .orElseGet(() -> {
                    log.warn("Nu s-a găsit strategie aplicabilă, folosind default");
                    return new StandardServicePriorityStrategy();
                });
    }
}
