package edu.saspsproject.strategy.recommendation;

import edu.saspsproject.dto.recommendation.RecommendationRequest;
import edu.saspsproject.dto.recommendation.InstitutionRecommendation;
import java.util.List;

/**
 * STRATEGY PATTERN - Interfața pentru algoritmi de recomandare
 * 
 * Fiecare strategie implementează un algoritm diferit pentru a recomanda
 * instituții utilizatorului bazat pe criterii specifice.
 * 
 * Avantaje:
 * - Algoritmi interschimbabili la runtime
 * - Ușor de adăugat noi strategii fără a modifica codul existent (Open/Closed)
 * - Fiecare strategie e izolată și testabilă independent
 * - Eliminarea condițiilor if-else multiple
 */
public interface RecommendationStrategy {
    
    /**
     * Returnează lista de instituții recomandate sortate după relevanță
     */
    List<InstitutionRecommendation> recommend(RecommendationRequest request);
    
    /**
     * Numele strategiei pentru logging și debugging
     */
    String getStrategyName();
    
    /**
     * Descrierea strategiei pentru UI
     */
    String getDescription();
}
