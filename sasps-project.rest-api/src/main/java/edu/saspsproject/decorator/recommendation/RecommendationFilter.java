package edu.saspsproject.decorator.recommendation;

import edu.saspsproject.dto.recommendation.InstitutionRecommendation;
import edu.saspsproject.dto.recommendation.RecommendationRequest;

import java.util.List;

/**
 * DECORATOR PATTERN - Interfață pentru filtre de recomandare
 * 
 * Permite adăugarea de filtre/modificări peste rezultatele de bază
 * ale strategiei de recomandare, fără a modifica strategia în sine.
 * 
 * Avantaje:
 * - Separarea responsabilităților (Single Responsibility)
 * - Filtre compozabile (poți combina mai multe)
 * - Nu modifică strategiile existente (Open/Closed)
 * - Flexibilitate la runtime
 */
public interface RecommendationFilter {
    
    /**
     * Aplică filtrul peste lista de recomandări
     */
    List<InstitutionRecommendation> apply(
        List<InstitutionRecommendation> recommendations, 
        RecommendationRequest request
    );
    
    /**
     * Ordinea de aplicare (mai mic = se aplică mai întâi)
     */
    default int getOrder() {
        return 100;
    }
    
    /**
     * Numele filtrului pentru logging
     */
    String getFilterName();
}
