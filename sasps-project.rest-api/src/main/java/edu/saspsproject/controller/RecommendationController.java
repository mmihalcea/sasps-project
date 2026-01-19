package edu.saspsproject.controller;

import edu.saspsproject.dto.recommendation.RecommendationRequest;
import edu.saspsproject.dto.recommendation.RecommendationResponse;
import edu.saspsproject.factory.recommendation.RecommendationStrategyFactory;
import edu.saspsproject.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * CONTROLLER - API pentru Motorul de Recomandări
 * 
 * Acest controller expune endpoint-uri pentru:
 * - Obținerea de recomandări personalizate
 * - Listarea strategiilor disponibile
 * 
 * Design Patterns folosite în flow:
 * - Factory Pattern: Crearea strategiei corecte
 * - Strategy Pattern: Algoritmi de recomandare interschimbabili
 * - Decorator Pattern: Filtre aplicate peste rezultate
 * - Template Method: Flow-ul comun de procesare
 */
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class RecommendationController {
    
    private final RecommendationService recommendationService;
    
    /**
     * Obține recomandări de instituții bazate pe criteriile specificate
     * 
     * @param serviceType Tipul serviciului dorit
     * @param userCounty Județul utilizatorului
     * @param strategy Strategia de recomandare (opțional)
     * @param maxResults Numărul maxim de rezultate (opțional, default: 5)
     * @param preferSameCounty Preferă instituții din același județ (opțional)
     */
    @GetMapping
    public ResponseEntity<RecommendationResponse> getRecommendations(
            @RequestParam String serviceType,
            @RequestParam(required = false) String userCounty,
            @RequestParam(required = false, defaultValue = "NEAREST_LOCATION") String strategy,
            @RequestParam(required = false, defaultValue = "5") Integer maxResults,
            @RequestParam(required = false, defaultValue = "false") boolean preferSameCounty) {
        
        log.info("📥 GET /api/recommendations - serviceType={}, county={}, strategy={}",
            serviceType, userCounty, strategy);
        
        RecommendationRequest request = RecommendationRequest.builder()
            .serviceType(serviceType)
            .userCounty(userCounty)
            .strategy(strategy)
            .maxResults(maxResults)
            .preferSameCounty(preferSameCounty)
            .build();
        
        RecommendationResponse response = recommendationService.getRecommendations(request);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Obține recomandări via POST (pentru request-uri complexe)
     */
    @PostMapping
    public ResponseEntity<RecommendationResponse> getRecommendationsPost(
            @RequestBody RecommendationRequest request) {
        
        log.info("📥 POST /api/recommendations - request={}", request);
        
        RecommendationResponse response = recommendationService.getRecommendations(request);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Listează strategiile de recomandare disponibile
     */
    @GetMapping("/strategies")
    public ResponseEntity<List<RecommendationStrategyFactory.StrategyInfo>> getStrategies() {
        log.info("📥 GET /api/recommendations/strategies");
        
        List<RecommendationStrategyFactory.StrategyInfo> strategies = 
            recommendationService.getAvailableStrategies();
        
        return ResponseEntity.ok(strategies);
    }
    
    /**
     * Endpoint de test pentru demo
     */
    @GetMapping("/demo")
    public ResponseEntity<Map<String, Object>> demo(
            @RequestParam(defaultValue = "DECLARATIE_FISCALA") String serviceType,
            @RequestParam(defaultValue = "BUCURESTI") String userCounty) {
        
        log.info("🎯 Demo mode: testez toate strategiile pentru {} din {}", 
            serviceType, userCounty);
        
        List<String> strategies = List.of(
            "NEAREST_LOCATION", 
            "FASTEST_AVAILABILITY", 
            "BEST_RATED", 
            "LEAST_BUSY"
        );
        
        Map<String, RecommendationResponse> results = new java.util.HashMap<>();
        
        for (String strategy : strategies) {
            RecommendationRequest request = RecommendationRequest.builder()
                .serviceType(serviceType)
                .userCounty(userCounty)
                .strategy(strategy)
                .maxResults(3)
                .build();
            
            results.put(strategy, recommendationService.getRecommendations(request));
        }
        
        return ResponseEntity.ok(Map.of(
            "serviceType", serviceType,
            "userCounty", userCounty,
            "results", results,
            "patternsUsed", List.of(
                "Strategy Pattern - 4 algoritmi diferiți",
                "Factory Pattern - Crearea strategiilor",
                "Decorator Pattern - Filtre compozabile",
                "Template Method - Flow procesare"
            )
        ));
    }
}
