package edu.saspsproject.strategy;

import edu.saspsproject.model.Appointment;
import org.springframework.stereotype.Component;

/**
 * STRATEGY PATTERN - Standard Service Priority Strategy
 * 
 * Strategie implicită pentru servicii standard.
 * Se aplică când nicio altă strategie specifică nu se potrivește.
 */
@Component
public class StandardServicePriorityStrategy implements PriorityStrategy {
    
    @Override
    public Appointment.PriorityLevel calculatePriority(Appointment appointment) {
        return Appointment.PriorityLevel.MEDIUM;
    }
    
    @Override
    public double calculateEstimatedDuration(Appointment appointment) {
        // Durată standard de 30 minute
        return 30.0;
    }
    
    @Override
    public boolean appliesTo(Appointment appointment) {
        // Se aplică pentru prioritate MEDIUM sau când nu e setată
        return appointment.getPriorityLevel() == Appointment.PriorityLevel.MEDIUM
                || appointment.getPriorityLevel() == null;
    }
    
    @Override
    public int getOrder() {
        return 50; // Prioritate medie
    }
}
