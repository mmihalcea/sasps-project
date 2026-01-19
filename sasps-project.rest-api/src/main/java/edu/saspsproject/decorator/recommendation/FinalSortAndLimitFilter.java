package edu.saspsproject.decorator.recommendation;

import edu.saspsproject.dto.recommendation.InstitutionRecommendation;
import edu.saspsproject.dto.recommendation.RecommendationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DECORATOR PATTERN - Filtru: Limită rezultate și sortare finală
 * 
 * Aplică sortarea finală după scor și limitează numărul de rezultate.
 * Se aplică ultimul în lanțul de filtre.
 */
@Component
@Slf4j
public class FinalSortAndLimitFilter implements RecommendationFilter {
    
    private static final int DEFAULT_LIMIT = 5;
    
    @Override
    public List<InstitutionRecommendation> apply(
            List<InstitutionRecommendation> recommendations,
            RecommendationRequest request) {
        
        int limit = request.getMaxResults() != null ? request.getMaxResults() : DEFAULT_LIMIT;
        
        log.info("🔢 FinalSortAndLimitFilter: Sortez și limitez la {} rezultate", limit);
        
        return recommendations.stream()
            .sorted(Comparator.comparingDouble(InstitutionRecommendation::getScore).reversed())
            .limit(limit)
            .peek(r -> {
                // Adaugă ranking
                int rank = recommendations.indexOf(r) + 1;
                r.setRank(rank);
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public int getOrder() {
        return 1000; // Se aplică ultimul
    }
    
    @Override
    public String getFilterName() {
        return "FINAL_SORT_LIMIT";
    }
}
