package edu.saspsproject.factory.recommendation;

import edu.saspsproject.strategy.recommendation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * FACTORY PATTERN - Creează strategii de recomandare
 * 
 * Această fabrică gestionează toate strategiile disponibile și le oferă
 * la cerere bazat pe tipul solicitat.
 * 
 * Avantaje:
 * - Centralizează crearea strategiilor
 * - Decuplează clientul de implementările concrete
 * - Ușor de extins cu noi strategii (doar înregistrare)
 * - Suportă auto-discovery prin Spring DI
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RecommendationStrategyFactory {
    
    private final List<RecommendationStrategy> allStrategies;
    
    /**
     * Returnează strategia cerută sau cea default
     */
    public RecommendationStrategy getStrategy(String strategyType) {
        if (strategyType == null || strategyType.isBlank()) {
            log.info("🏭 Factory: Nu s-a specificat strategia, folosesc BALANCED");
            return getDefaultStrategy();
        }
        
        return allStrategies.stream()
            .filter(s -> s.getStrategyName().equalsIgnoreCase(strategyType))
            .findFirst()
            .orElseGet(() -> {
                log.warn("🏭 Factory: Strategia '{}' nu există, folosesc default", strategyType);
                return getDefaultStrategy();
            });
    }
    
    /**
     * Returnează strategia default (NEAREST_LOCATION)
     */
    public RecommendationStrategy getDefaultStrategy() {
        return allStrategies.stream()
            .filter(s -> s.getStrategyName().equals("NEAREST_LOCATION"))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Nu există strategie default!"));
    }
    
    /**
     * Returnează toate strategiile disponibile (pentru UI dropdown)
     */
    public List<StrategyInfo> getAvailableStrategies() {
        return allStrategies.stream()
            .map(s -> new StrategyInfo(s.getStrategyName(), s.getDescription()))
            .collect(Collectors.toList());
    }
    
    /**
     * Returnează un map pentru acces rapid
     */
    public Map<String, RecommendationStrategy> getStrategyMap() {
        return allStrategies.stream()
            .collect(Collectors.toMap(
                RecommendationStrategy::getStrategyName,
                Function.identity()
            ));
    }
    
    /**
     * DTO pentru informații despre strategie
     */
    public record StrategyInfo(String name, String description) {}
}
