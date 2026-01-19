package edu.saspsproject.template.recommendation;

import edu.saspsproject.decorator.recommendation.RecommendationFilter;
import edu.saspsproject.dto.recommendation.InstitutionRecommendation;
import edu.saspsproject.dto.recommendation.RecommendationRequest;
import edu.saspsproject.dto.recommendation.RecommendationResponse;
import edu.saspsproject.strategy.recommendation.RecommendationStrategy;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

/**
 * TEMPLATE METHOD PATTERN - Definește scheletul algoritmului de recomandare
 * 
 * Acest pattern definește pașii comuni pentru toate procesele de recomandare,
 * permițând subclaselor să redefinească anumiți pași fără a schimba structura.
 * 
 * Avantaje:
 * - Reutilizare cod comun (DRY)
 * - Structură consistentă pentru toate tipurile de recomandări
 * - Ușor de extins cu noi tipuri de procesare
 * - Hooks pentru personalizare
 * 
 * Pași:
 * 1. Validare request
 * 2. Pre-procesare (hook)
 * 3. Execuție strategie
 * 4. Aplicare filtre
 * 5. Post-procesare (hook)
 * 6. Construire răspuns
 */
@Slf4j
public abstract class RecommendationTemplate {
    
    /**
     * Template Method - definește ordinea pașilor
     * Metoda finală nu poate fi suprascrisă
     */
    public final RecommendationResponse processRecommendation(
            RecommendationRequest request,
            RecommendationStrategy strategy,
            List<RecommendationFilter> filters) {
        
        Instant start = Instant.now();
        log.info("🚀 Template: Încep procesarea recomandărilor cu strategia {}", 
            strategy.getStrategyName());
        
        try {
            // Pas 1: Validare
            validateRequest(request);
            
            // Pas 2: Pre-procesare (hook pentru subclase)
            request = preProcess(request);
            
            // Pas 3: Execuție strategie
            List<InstitutionRecommendation> recommendations = executeStrategy(strategy, request);
            log.info("📋 Template: Strategia a returnat {} recomandări", recommendations.size());
            
            // Pas 4: Aplicare filtre în ordine
            recommendations = applyFilters(recommendations, request, filters);
            log.info("🔍 Template: După filtre rămân {} recomandări", recommendations.size());
            
            // Pas 5: Post-procesare (hook pentru subclase)
            recommendations = postProcess(recommendations, request);
            
            // Pas 6: Construire răspuns
            Duration processingTime = Duration.between(start, Instant.now());
            return buildResponse(recommendations, strategy, processingTime);
            
        } catch (Exception e) {
            log.error("❌ Template: Eroare în procesarea recomandărilor: {}", e.getMessage(), e);
            throw new RecommendationException("Eroare la generarea recomandărilor", e);
        }
    }
    
    /**
     * Validează request-ul de recomandare
     */
    protected void validateRequest(RecommendationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request-ul nu poate fi null");
        }
        if (request.getServiceType() == null || request.getServiceType().isBlank()) {
            throw new IllegalArgumentException("Tipul serviciului este obligatoriu");
        }
        log.debug("✅ Request validat cu succes");
    }
    
    /**
     * Hook pentru pre-procesare - poate fi suprascris de subclase
     */
    protected RecommendationRequest preProcess(RecommendationRequest request) {
        // Default: setează valori implicite
        if (request.getMaxResults() == null || request.getMaxResults() <= 0) {
            request.setMaxResults(5);
        }
        return request;
    }
    
    /**
     * Execută strategia de recomandare
     */
    protected List<InstitutionRecommendation> executeStrategy(
            RecommendationStrategy strategy,
            RecommendationRequest request) {
        return strategy.recommend(request);
    }
    
    /**
     * Aplică filtrele în ordinea specificată
     */
    protected List<InstitutionRecommendation> applyFilters(
            List<InstitutionRecommendation> recommendations,
            RecommendationRequest request,
            List<RecommendationFilter> filters) {
        
        // Sortăm filtrele după ordine
        List<RecommendationFilter> sortedFilters = filters.stream()
            .sorted(Comparator.comparingInt(RecommendationFilter::getOrder))
            .toList();
        
        for (RecommendationFilter filter : sortedFilters) {
            log.debug("🔧 Aplic filtrul: {}", filter.getFilterName());
            recommendations = filter.apply(recommendations, request);
        }
        
        return recommendations;
    }
    
    /**
     * Hook pentru post-procesare - poate fi suprascris de subclase
     */
    protected List<InstitutionRecommendation> postProcess(
            List<InstitutionRecommendation> recommendations,
            RecommendationRequest request) {
        // Adaugă ranking final
        for (int i = 0; i < recommendations.size(); i++) {
            recommendations.get(i).setRank(i + 1);
        }
        return recommendations;
    }
    
    /**
     * Construiește răspunsul final - metodă abstractă, trebuie implementată
     */
    protected abstract RecommendationResponse buildResponse(
        List<InstitutionRecommendation> recommendations,
        RecommendationStrategy strategy,
        Duration processingTime
    );
    
    /**
     * Excepție custom pentru erori de recomandare
     */
    public static class RecommendationException extends RuntimeException {
        public RecommendationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
