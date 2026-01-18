package edu.saspsproject.strategy;

import edu.saspsproject.model.Appointment;

/**
 * STRATEGY PATTERN - Priority Calculation Strategy
 * 
 * Definește contractul pentru diferite strategii de calculare a priorității.
 * Permite adăugarea de noi reguli de prioritizare fără modificarea codului existent.
 * 
 * Beneficii:
 * - Încapsulează algoritmi de prioritizare
 * - Permite schimbarea strategiei la runtime
 * - Respectă Open/Closed Principle
 * - Facilitează testarea independentă a fiecărei strategii
 * 
 * @see UrgentServicePriorityStrategy
 * @see DocumentComplexityPriorityStrategy
 * @see UserHistoryPriorityStrategy
 */
public interface PriorityStrategy {
    
    /**
     * Calculează nivelul de prioritate pentru o programare.
     * 
     * @param appointment programarea pentru care se calculează prioritatea
     * @return nivelul de prioritate calculat
     */
    Appointment.PriorityLevel calculatePriority(Appointment appointment);
    
    /**
     * Calculează durata estimată pentru o programare.
     * 
     * @param appointment programarea pentru care se calculează durata
     * @return durata estimată în minute
     */
    double calculateEstimatedDuration(Appointment appointment);
    
    /**
     * Verifică dacă această strategie se aplică pentru programarea dată.
     * 
     * @param appointment programarea de verificat
     * @return true dacă strategia se aplică
     */
    boolean appliesTo(Appointment appointment);
    
    /**
     * Returnează ordinea de prioritate a strategiei.
     * Strategiile cu ordine mai mică sunt evaluate primele.
     * 
     * @return ordinea (default 100)
     */
    default int getOrder() {
        return 100;
    }
}
