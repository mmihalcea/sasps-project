package edu.saspsproject.strategy;

import edu.saspsproject.model.Appointment;
import org.springframework.stereotype.Component;

/**
 * STRATEGY PATTERN - Urgent Service Priority Strategy
 * 
 * Strategie care acordă prioritate mare serviciilor marcate ca urgente.
 * Se aplică când prioritatea este setată explicit ca URGENT sau HIGH.
 */
@Component
public class UrgentServicePriorityStrategy implements PriorityStrategy {
    
    @Override
    public Appointment.PriorityLevel calculatePriority(Appointment appointment) {
        // Serviciile urgente primesc întotdeauna prioritate maximă
        if (appointment.getPriorityLevel() == Appointment.PriorityLevel.URGENT) {
            return Appointment.PriorityLevel.URGENT;
        }
        return Appointment.PriorityLevel.HIGH;
    }
    
    @Override
    public double calculateEstimatedDuration(Appointment appointment) {
        // Serviciile urgente sunt procesate mai rapid (15 minute)
        return 15.0;
    }
    
    @Override
    public boolean appliesTo(Appointment appointment) {
        return appointment.getPriorityLevel() == Appointment.PriorityLevel.URGENT
                || appointment.getPriorityLevel() == Appointment.PriorityLevel.HIGH;
    }
    
    @Override
    public int getOrder() {
        return 1; // Prima strategie verificată
    }
}
