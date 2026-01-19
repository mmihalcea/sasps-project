package edu.saspsproject.strategy.recommendation;

import edu.saspsproject.dto.recommendation.RecommendationRequest;
import edu.saspsproject.dto.recommendation.InstitutionRecommendation;
import edu.saspsproject.model.Appointment;
import edu.saspsproject.model.Institution;
import edu.saspsproject.repository.AppointmentRepository;
import edu.saspsproject.repository.InstitutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * STRATEGY PATTERN - Implementare: Recomandare bazată pe disponibilitate rapidă
 * 
 * Recomandă instituțiile care au cel mai apropiat slot liber disponibil.
 * Ideal pentru utilizatori care vor să fie serviți cât mai repede.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FastestAvailabilityStrategy implements RecommendationStrategy {
    
    private final InstitutionRepository institutionRepository;
    private final AppointmentRepository appointmentRepository;
    
    @Override
    public List<InstitutionRecommendation> recommend(RecommendationRequest request) {
        log.info("⚡ FastestAvailabilityStrategy: Caut cel mai rapid slot disponibil");
        
        List<Institution> institutions = institutionRepository.findByServiceType(request.getServiceType());
        LocalDateTime now = LocalDateTime.now();
        
        return institutions.stream()
            .map(inst -> {
                LocalDateTime nextAvailable = findNextAvailableSlot(inst, now);
                long hoursUntilAvailable = ChronoUnit.HOURS.between(now, nextAvailable);
                
                // Scorul e invers proporțional cu timpul de așteptare
                double score = Math.max(0, 100 - (hoursUntilAvailable * 2)); // -2 puncte per oră
                
                String reason;
                if (hoursUntilAvailable < 24) {
                    reason = String.format("Disponibil în %d ore", hoursUntilAvailable);
                } else {
                    long days = hoursUntilAvailable / 24;
                    reason = String.format("Disponibil în %d zile", days);
                }
                
                return InstitutionRecommendation.builder()
                    .institutionId(inst.getId())
                    .institutionName(inst.getName())
                    .county(inst.getCounty().getName())
                    .address(inst.getAddress())
                    .score(score)
                    .nextAvailableSlot(nextAvailable)
                    .waitTimeHours((int) hoursUntilAvailable)
                    .reason(reason)
                    .build();
            })
            .sorted(Comparator.comparingInt(InstitutionRecommendation::getWaitTimeHours))
            .limit(request.getMaxResults() != null ? request.getMaxResults() : 5)
            .collect(Collectors.toList());
    }
    
    /**
     * Găsește următorul slot disponibil pentru o instituție
     */
    private LocalDateTime findNextAvailableSlot(Institution institution, LocalDateTime from) {
        LocalDate currentDate = from.toLocalDate();
        LocalTime startHour = LocalTime.of(8, 0);
        LocalTime endHour = LocalTime.of(16, 0);
        int slotDurationMinutes = 30;
        
        // Căutăm în următoarele 30 de zile
        for (int dayOffset = 0; dayOffset < 30; dayOffset++) {
            LocalDate checkDate = currentDate.plusDays(dayOffset);
            
            // Skip weekend
            if (checkDate.getDayOfWeek().getValue() >= 6) {
                continue;
            }
            
            List<Appointment> dayAppointments = appointmentRepository
                .findByInstitutionIdAndAppointmentTimeBetween(
                    institution.getId(),
                    checkDate.atTime(startHour),
                    checkDate.atTime(endHour)
                );
            
            // Verifică fiecare slot din zi
            LocalTime currentSlot = startHour;
            while (currentSlot.isBefore(endHour)) {
                LocalDateTime slotDateTime = checkDate.atTime(currentSlot);
                
                // Skip sloturi din trecut
                if (slotDateTime.isBefore(from)) {
                    currentSlot = currentSlot.plusMinutes(slotDurationMinutes);
                    continue;
                }
                
                // Verifică dacă slotul e liber
                final LocalTime checkSlot = currentSlot;
                boolean isOccupied = dayAppointments.stream()
                    .anyMatch(apt -> apt.getAppointmentTime().toLocalTime().equals(checkSlot));
                
                if (!isOccupied) {
                    return slotDateTime;
                }
                
                currentSlot = currentSlot.plusMinutes(slotDurationMinutes);
            }
        }
        
        // Dacă nu găsim nimic, returnăm peste 30 de zile
        return from.plusDays(30);
    }
    
    @Override
    public String getStrategyName() {
        return "FASTEST_AVAILABILITY";
    }
    
    @Override
    public String getDescription() {
        return "Recomandă instituțiile cu cel mai rapid slot disponibil";
    }
}
