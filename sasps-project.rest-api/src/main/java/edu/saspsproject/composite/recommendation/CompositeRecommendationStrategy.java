package edu.saspsproject.composite.recommendation;

import edu.saspsproject.dto.recommendation.InstitutionRecommendation;
import edu.saspsproject.dto.recommendation.RecommendationRequest;
import edu.saspsproject.strategy.recommendation.RecommendationStrategy;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * COMPOSITE PATTERN - Combină multiple strategii de recomandare cu ponderi
 * 
 * Permite utilizatorului să definească importanța fiecărui criteriu:
 * - 40% distanță + 30% rating + 30% disponibilitate
 * 
 * Calculează un scor ponderat final pentru fiecare instituție.
 */
@Getter
public class CompositeRecommendationStrategy implements RecommendationStrategy {
    
    private final Map<RecommendationStrategy, Double> weightedStrategies;
    private final String name;
    
    public CompositeRecommendationStrategy() {
        this.weightedStrategies = new HashMap<>();
        this.name = "COMPOSITE";
    }
    
    /**
     * Adaugă o strategie cu o pondere specifică
     * @param strategy Strategia de adăugat
     * @param weight Ponderea (0.0 - 1.0)
     */
    public void addStrategy(RecommendationStrategy strategy, Double weight) {
        if (weight < 0 || weight > 1) {
            throw new IllegalArgumentException("Ponderea trebuie să fie între 0 și 1");
        }
        weightedStrategies.put(strategy, weight);
    }
    
    /**
     * Elimină o strategie din composite
     */
    public void removeStrategy(RecommendationStrategy strategy) {
        weightedStrategies.remove(strategy);
    }
    
    /**
     * Verifică dacă ponderile totale sunt valide (suma = 1.0)
     */
    public boolean isValid() {
        double totalWeight = weightedStrategies.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
        return Math.abs(totalWeight - 1.0) < 0.01;
    }
    
    /**
     * Normalizează ponderile pentru a se asigura că suma = 1.0
     */
    public void normalizeWeights() {
        double totalWeight = weightedStrategies.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
        
        if (totalWeight > 0) {
            Map<RecommendationStrategy, Double> normalized = new HashMap<>();
            for (Map.Entry<RecommendationStrategy, Double> entry : weightedStrategies.entrySet()) {
                normalized.put(entry.getKey(), entry.getValue() / totalWeight);
            }
            weightedStrategies.clear();
            weightedStrategies.putAll(normalized);
        }
    }
    
    @Override
    public List<InstitutionRecommendation> recommend(RecommendationRequest request) {
        
        if (weightedStrategies.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Normalizează ponderile dacă nu sunt valide
        if (!isValid()) {
            normalizeWeights();
        }
        
        // Colectează scorurile de la fiecare strategie
        Map<Long, Map<String, Double>> allScores = new HashMap<>();
        Map<Long, InstitutionRecommendation> bestRecommendations = new HashMap<>();
        
        // Calculează scorurile pentru fiecare strategie
        for (Map.Entry<RecommendationStrategy, Double> entry : weightedStrategies.entrySet()) {
            RecommendationStrategy strategy = entry.getKey();
            Double weight = entry.getValue();
            
            List<InstitutionRecommendation> strategyResults = strategy.recommend(request);
            
            for (InstitutionRecommendation rec : strategyResults) {
                Long instId = rec.getInstitutionId();
                
                // Inițializează dacă nu există
                allScores.computeIfAbsent(instId, k -> new HashMap<>());
                
                // Salvează scorul ponderat
                allScores.get(instId).put(strategy.getStrategyName(), rec.getScore() * weight);
                
                // Păstrează cea mai bună recomandare pentru datele de bază
                if (!bestRecommendations.containsKey(instId)) {
                    bestRecommendations.put(instId, rec);
                }
            }
        }
        
        // Calculează scorul final ponderat
        List<InstitutionRecommendation> results = new ArrayList<>();
        
        for (Map.Entry<Long, Map<String, Double>> entry : allScores.entrySet()) {
            Long institutionId = entry.getKey();
            Map<String, Double> scores = entry.getValue();
            InstitutionRecommendation baseRec = bestRecommendations.get(institutionId);
            
            // Suma ponderată a scorurilor
            double finalScore = scores.values().stream()
                    .mapToDouble(Double::doubleValue)
                    .sum();
            
            // Construiește breakdown-ul detaliat
            StringBuilder reason = new StringBuilder();
            reason.append("Scor Compozit: ");
            
            List<String> parts = new ArrayList<>();
            for (Map.Entry<RecommendationStrategy, Double> stratEntry : weightedStrategies.entrySet()) {
                String stratName = stratEntry.getKey().getStrategyName();
                Double weight = stratEntry.getValue();
                Double scoreContribution = scores.getOrDefault(stratName, 0.0);
                parts.add(String.format("%s %.0f%%", 
                        getStrategyDisplayName(stratName), 
                        weight * 100));
            }
            reason.append(String.join(" + ", parts));
            
            // Construiește recomandarea finală
            InstitutionRecommendation rec = InstitutionRecommendation.builder()
                    .institutionId(institutionId)
                    .institutionName(baseRec.getInstitutionName())
                    .address(baseRec.getAddress())
                    .county(baseRec.getCounty())
                    .score(finalScore)
                    .reason(reason.toString())
                    .distance(baseRec.getDistance())
                    .rating(baseRec.getRating())
                    .totalReviews(baseRec.getTotalReviews())
                    .waitTimeHours(baseRec.getWaitTimeHours())
                    .nextAvailableSlot(baseRec.getNextAvailableSlot())
                    .occupancyRate(baseRec.getOccupancyRate())
                    .availableSlots(baseRec.getAvailableSlots())
                    .boosted(true)
                    .boostReason("Composite: " + parts.size() + " strategii combinate")
                    .build();
            
            results.add(rec);
        }
        
        // Sortează după scorul final și asignează rank-uri
        results.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        
        for (int i = 0; i < results.size(); i++) {
            results.get(i).setRank(i + 1);
        }
        
        return results;
    }
    
    private String getStrategyDisplayName(String strategyName) {
        return switch (strategyName) {
            case "NEAREST_LOCATION" -> "Distanță";
            case "FASTEST_AVAILABILITY" -> "Disponibilitate";
            case "BEST_RATED" -> "Rating";
            case "LEAST_BUSY" -> "Aglomerație";
            default -> strategyName;
        };
    }
    
    @Override
    public String getStrategyName() {
        return name;
    }
    
    @Override
    public String getDescription() {
        if (weightedStrategies.isEmpty()) {
            return "Strategie compozită - configurați ponderile";
        }
        
        StringBuilder desc = new StringBuilder("Combinație: ");
        List<String> parts = new ArrayList<>();
        
        for (Map.Entry<RecommendationStrategy, Double> entry : weightedStrategies.entrySet()) {
            parts.add(String.format("%s (%.0f%%)", 
                    getStrategyDisplayName(entry.getKey().getStrategyName()), 
                    entry.getValue() * 100));
        }
        
        desc.append(String.join(" + ", parts));
        return desc.toString();
    }
}
