package edu.saspsproject.decorator.recommendation;

import edu.saspsproject.dto.recommendation.InstitutionRecommendation;
import edu.saspsproject.dto.recommendation.RecommendationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DECORATOR PATTERN - Filtru: Doar instituții din județul utilizatorului
 * 
 * Restricționează rezultatele la instituțiile din același județ cu utilizatorul.
 */
@Component
@Slf4j
public class SameCountyFilter implements RecommendationFilter {
    
    @Override
    public List<InstitutionRecommendation> apply(
            List<InstitutionRecommendation> recommendations,
            RecommendationRequest request) {
        
        if (request.getUserCounty() == null || !request.isPreferSameCounty()) {
            return recommendations;
        }
        
        log.info("🎯 SameCountyFilter: Filtrez pentru județul {}", request.getUserCounty());
        
        List<InstitutionRecommendation> filtered = recommendations.stream()
            .filter(r -> r.getCounty().equalsIgnoreCase(request.getUserCounty()))
            .collect(Collectors.toList());
        
        // Dacă nu găsim nimic în același județ, returnăm originalul
        if (filtered.isEmpty()) {
            log.info("🎯 SameCountyFilter: Nu sunt instituții în județ, returnez toate");
            return recommendations;
        }
        
        return filtered;
    }
    
    @Override
    public int getOrder() {
        return 10; // Se aplică primul
    }
    
    @Override
    public String getFilterName() {
        return "SAME_COUNTY";
    }
}
