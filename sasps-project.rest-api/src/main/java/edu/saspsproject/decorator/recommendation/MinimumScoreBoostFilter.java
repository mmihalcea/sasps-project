package edu.saspsproject.decorator.recommendation;

import edu.saspsproject.dto.recommendation.InstitutionRecommendation;
import edu.saspsproject.dto.recommendation.RecommendationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * DECORATOR PATTERN - Filtru: Boost pentru scor minim
 * 
 * Ajustează scorurile pentru a promova instituțiile care îndeplinesc
 * un prag minim de calitate.
 */
@Component
@Slf4j
public class MinimumScoreBoostFilter implements RecommendationFilter {
    
    private static final double BOOST_THRESHOLD = 70.0;
    private static final double BOOST_AMOUNT = 10.0;
    
    @Override
    public List<InstitutionRecommendation> apply(
            List<InstitutionRecommendation> recommendations,
            RecommendationRequest request) {
        
        log.info("📈 MinimumScoreBoostFilter: Boost pentru instituții cu scor > {}", BOOST_THRESHOLD);
        
        recommendations.forEach(r -> {
            if (r.getScore() >= BOOST_THRESHOLD) {
                double boostedScore = Math.min(100, r.getScore() + BOOST_AMOUNT);
                r.setScore(boostedScore);
                r.setBoosted(true);
                r.setBoostReason("Instituție de top");
            }
        });
        
        return recommendations;
    }
    
    @Override
    public int getOrder() {
        return 50;
    }
    
    @Override
    public String getFilterName() {
        return "MINIMUM_SCORE_BOOST";
    }
}
