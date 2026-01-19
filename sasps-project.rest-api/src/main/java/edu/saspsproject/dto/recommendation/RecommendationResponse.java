package edu.saspsproject.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pentru răspunsul cu recomandări
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponse {
    
    /**
     * Lista de instituții recomandate, sortate după scor
     */
    private List<InstitutionRecommendation> recommendations;
    
    /**
     * Numărul total de rezultate
     */
    private int totalResults;
    
    /**
     * Strategia folosită pentru recomandări
     */
    private String strategyUsed;
    
    /**
     * Descrierea strategiei
     */
    private String strategyDescription;
    
    /**
     * Timpul de procesare în milisecunde
     */
    private long processingTimeMs;
    
    /**
     * Momentul generării răspunsului
     */
    private LocalDateTime generatedAt;
}
