package edu.saspsproject.strategy.recommendation;

import edu.saspsproject.dto.recommendation.RecommendationRequest;
import edu.saspsproject.dto.recommendation.InstitutionRecommendation;
import edu.saspsproject.model.Institution;
import edu.saspsproject.repository.AppointmentRepository;
import edu.saspsproject.repository.InstitutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * STRATEGY PATTERN - Implementare: Recomandare bazată pe gradul de ocupare
 * 
 * Recomandă instituțiile cel mai puțin aglomerate în următoarea săptămână.
 * Ideal pentru utilizatori care vor o experiență mai relaxată.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LeastBusyStrategy implements RecommendationStrategy {
    
    private final InstitutionRepository institutionRepository;
    private final AppointmentRepository appointmentRepository;
    
    // Capacitate teoretică: 8 ore × 2 sloturi/oră × 5 zile = 80 sloturi/săptămână
    private static final int WEEKLY_CAPACITY = 80;
    
    @Override
    public List<InstitutionRecommendation> recommend(RecommendationRequest request) {
        log.info("📊 LeastBusyStrategy: Analizez gradul de ocupare");
        
        List<Institution> institutions = institutionRepository.findByServiceType(request.getServiceType());
        LocalDateTime weekStart = LocalDateTime.now();
        LocalDateTime weekEnd = weekStart.plusDays(7);
        
        return institutions.stream()
            .map(inst -> {
                // Numărăm programările din următoarea săptămână
                long appointmentsCount = appointmentRepository
                    .countByInstitutionIdAndAppointmentTimeBetween(
                        inst.getId(), 
                        weekStart, 
                        weekEnd
                    );
                
                double occupancyRate = (double) appointmentsCount / WEEKLY_CAPACITY * 100;
                double availabilityRate = 100 - occupancyRate;
                
                // Scorul e proporțional cu disponibilitatea
                double score = availabilityRate;
                
                String reason;
                if (occupancyRate < 30) {
                    reason = String.format("Foarte liber (%.0f%% ocupat)", occupancyRate);
                } else if (occupancyRate < 60) {
                    reason = String.format("Moderat ocupat (%.0f%%)", occupancyRate);
                } else if (occupancyRate < 85) {
                    reason = String.format("Destul de ocupat (%.0f%%)", occupancyRate);
                } else {
                    reason = String.format("Foarte aglomerat (%.0f%% ocupat)", occupancyRate);
                }
                
                return InstitutionRecommendation.builder()
                    .institutionId(inst.getId())
                    .institutionName(inst.getName())
                    .county(inst.getCounty().getName())
                    .address(inst.getAddress())
                    .score(score)
                    .occupancyRate(occupancyRate)
                    .availableSlots((int) (WEEKLY_CAPACITY - appointmentsCount))
                    .reason(reason)
                    .build();
            })
            .sorted(Comparator.comparingDouble(InstitutionRecommendation::getOccupancyRate))
            .limit(request.getMaxResults() != null ? request.getMaxResults() : 5)
            .collect(Collectors.toList());
    }
    
    @Override
    public String getStrategyName() {
        return "LEAST_BUSY";
    }
    
    @Override
    public String getDescription() {
        return "Recomandă instituțiile cel mai puțin aglomerate";
    }
}
