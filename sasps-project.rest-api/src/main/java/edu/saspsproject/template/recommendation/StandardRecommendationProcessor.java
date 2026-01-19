package edu.saspsproject.template.recommendation;

import edu.saspsproject.dto.recommendation.InstitutionRecommendation;
import edu.saspsproject.dto.recommendation.RecommendationResponse;
import edu.saspsproject.strategy.recommendation.RecommendationStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TEMPLATE METHOD PATTERN - Implementare concretă pentru recomandări standard
 * 
 * Extinde template-ul de bază și implementează metoda abstractă buildResponse.
 */
@Component
@Slf4j
public class StandardRecommendationProcessor extends RecommendationTemplate {
    
    @Override
    protected RecommendationResponse buildResponse(
            List<InstitutionRecommendation> recommendations,
            RecommendationStrategy strategy,
            Duration processingTime) {
        
        log.info("📦 Construiesc răspunsul cu {} recomandări în {}ms",
            recommendations.size(), processingTime.toMillis());
        
        return RecommendationResponse.builder()
            .recommendations(recommendations)
            .totalResults(recommendations.size())
            .strategyUsed(strategy.getStrategyName())
            .strategyDescription(strategy.getDescription())
            .processingTimeMs(processingTime.toMillis())
            .generatedAt(LocalDateTime.now())
            .build();
    }
}
