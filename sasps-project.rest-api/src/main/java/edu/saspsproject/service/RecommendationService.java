package edu.saspsproject.service;

import edu.saspsproject.decorator.recommendation.RecommendationFilter;
import edu.saspsproject.dto.recommendation.RecommendationRequest;
import edu.saspsproject.dto.recommendation.RecommendationResponse;
import edu.saspsproject.factory.recommendation.RecommendationStrategyFactory;
import edu.saspsproject.strategy.recommendation.RecommendationStrategy;
import edu.saspsproject.template.recommendation.StandardRecommendationProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * SERVICE - Orchestrează motorul de recomandări
 * 
 * Folosește:
 * - Factory Pattern: pentru a obține strategia corectă
 * - Strategy Pattern: pentru algoritmi interschimbabili
 * - Decorator Pattern: pentru filtre adiționale
 * - Template Method: pentru flow-ul de procesare
 * 
 * Aceasta demonstrează puterea combinării design patterns:
 * - Codul e modular și ușor de testat
 * - Fiecare responsabilitate e separată
 * - Ușor de extins fără modificări
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {
    
    private final RecommendationStrategyFactory strategyFactory;
    private final StandardRecommendationProcessor processor;
    private final List<RecommendationFilter> filters;
    
    /**
     * Generează recomandări de instituții bazate pe request
     */
    public RecommendationResponse getRecommendations(RecommendationRequest request) {
        log.info("📍 RecommendationService: Primesc cerere pentru {} cu strategia {}",
            request.getServiceType(), request.getStrategy());
        
        // Factory Pattern - obține strategia
        RecommendationStrategy strategy = strategyFactory.getStrategy(request.getStrategy());
        log.info("🏭 Strategie selectată: {} - {}", 
            strategy.getStrategyName(), strategy.getDescription());
        
        // Template Method + Decorator - procesează recomandările
        RecommendationResponse response = processor.processRecommendation(request, strategy, filters);
        
        log.info("✅ Recomandări generate: {} rezultate în {}ms",
            response.getTotalResults(), response.getProcessingTimeMs());
        
        return response;
    }
    
    /**
     * Returnează lista de strategii disponibile pentru UI
     */
    public List<RecommendationStrategyFactory.StrategyInfo> getAvailableStrategies() {
        return strategyFactory.getAvailableStrategies();
    }
}
