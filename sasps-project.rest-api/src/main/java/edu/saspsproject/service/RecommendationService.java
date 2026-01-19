package edu.saspsproject.service;

import edu.saspsproject.composite.recommendation.CompositeRecommendationStrategy;
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
import java.util.Map;

/**
 * SERVICE - Orchestrează motorul de recomandări
 * 
 * Folosește:
 * - Factory Pattern: pentru a obține strategia corectă
 * - Strategy Pattern: pentru algoritmi interschimbabili
 * - Decorator Pattern: pentru filtre adiționale
 * - Template Method: pentru flow-ul de procesare
 * - Composite Pattern: pentru combinarea strategiilor cu ponderi
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
        
        RecommendationStrategy strategy;
        
        // COMPOSITE PATTERN - Verifică dacă avem ponderi pentru combinare
        if ("COMPOSITE".equals(request.getStrategy()) && request.getStrategyWeights() != null) {
            strategy = buildCompositeStrategy(request.getStrategyWeights());
            log.info("🎯 COMPOSITE Strategy creat cu {} strategii combinate", 
                request.getStrategyWeights().size());
        } else {
            // Factory Pattern - obține strategia simplă
            strategy = strategyFactory.getStrategy(request.getStrategy());
        }
        
        log.info("🏭 Strategie selectată: {} - {}", 
            strategy.getStrategyName(), strategy.getDescription());
        
        // Template Method + Decorator - procesează recomandările
        RecommendationResponse response = processor.processRecommendation(request, strategy, filters);
        
        log.info("✅ Recomandări generate: {} rezultate în {}ms",
            response.getTotalResults(), response.getProcessingTimeMs());
        
        return response;
    }
    
    /**
     * COMPOSITE PATTERN - Construiește o strategie compozită din ponderi
     */
    private CompositeRecommendationStrategy buildCompositeStrategy(Map<String, Integer> weights) {
        CompositeRecommendationStrategy composite = new CompositeRecommendationStrategy();
        
        for (Map.Entry<String, Integer> entry : weights.entrySet()) {
            String strategyName = entry.getKey();
            Integer weight = entry.getValue();
            
            if (weight > 0) {
                try {
                    RecommendationStrategy strategy = strategyFactory.getStrategy(strategyName);
                    composite.addStrategy(strategy, weight / 100.0);
                    log.debug("➕ Adăugat {} cu pondere {}%", strategyName, weight);
                } catch (Exception e) {
                    log.warn("⚠️ Nu am putut adăuga strategia {}: {}", strategyName, e.getMessage());
                }
            }
        }
        
        // Normalizează pentru a ne asigura că suma = 100%
        composite.normalizeWeights();
        
        return composite;
    }
    
    /**
     * Returnează lista de strategii disponibile pentru UI
     */
    public List<RecommendationStrategyFactory.StrategyInfo> getAvailableStrategies() {
        return strategyFactory.getAvailableStrategies();
    }
}
