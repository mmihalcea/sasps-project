package edu.saspsproject.strategy;

import edu.saspsproject.model.Appointment;
import org.springframework.stereotype.Component;

/**
 * STRATEGY PATTERN - Low Priority Service Strategy
 * 
 * Strategie pentru servicii cu prioritate scăzută.
 * Aceste servicii pot necesita mai mult timp și nu sunt urgente.
 */
@Component
public class LowPriorityServiceStrategy implements PriorityStrategy {
    
    @Override
    public Appointment.PriorityLevel calculatePriority(Appointment appointment) {
        return Appointment.PriorityLevel.LOW;
    }
    
    @Override
    public double calculateEstimatedDuration(Appointment appointment) {
        // Durată mai mare pentru servicii complexe dar non-urgente
        return 45.0;
    }
    
    @Override
    public boolean appliesTo(Appointment appointment) {
        return appointment.getPriorityLevel() == Appointment.PriorityLevel.LOW;
    }
    
    @Override
    public int getOrder() {
        return 100; // Verificat ultimul
    }
}
