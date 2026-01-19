package edu.saspsproject.strategy.recommendation;

import edu.saspsproject.dto.recommendation.RecommendationRequest;
import edu.saspsproject.dto.recommendation.InstitutionRecommendation;
import edu.saspsproject.model.Institution;
import edu.saspsproject.repository.InstitutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * STRATEGY PATTERN - Implementare: Recomandare bazată pe rating
 * 
 * Recomandă instituțiile cu cel mai bun rating de la utilizatori.
 * Folosește Bayesian Average pentru a evita bias-ul instituțiilor cu puține review-uri.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BestRatedStrategy implements RecommendationStrategy {
    
    private final InstitutionRepository institutionRepository;
    
    // Parametri pentru Bayesian Average
    private static final double GLOBAL_AVERAGE = 3.5; // Rating mediu global
    private static final int CONFIDENCE_THRESHOLD = 10; // Număr minim de review-uri pentru încredere
    
    @Override
    public List<InstitutionRecommendation> recommend(RecommendationRequest request) {
        log.info("⭐ BestRatedStrategy: Sortez după rating");
        
        List<Institution> institutions = institutionRepository.findByServiceType(request.getServiceType());
        
        return institutions.stream()
            .map(inst -> {
                // Simulăm rating-uri (în producție ar veni din baza de date)
                RatingData rating = generateSimulatedRating(inst.getId());
                double bayesianScore = calculateBayesianAverage(rating.averageRating, rating.totalReviews);
                
                String reason = String.format("%.1f ⭐ (%d recenzii)", rating.averageRating, rating.totalReviews);
                
                return InstitutionRecommendation.builder()
                    .institutionId(inst.getId())
                    .institutionName(inst.getName())
                    .county(inst.getCounty().getName())
                    .address(inst.getAddress())
                    .score(bayesianScore * 20) // Convertim la scală 0-100
                    .rating(rating.averageRating)
                    .totalReviews(rating.totalReviews)
                    .reason(reason)
                    .build();
            })
            .sorted(Comparator.comparingDouble(InstitutionRecommendation::getScore).reversed())
            .limit(request.getMaxResults() != null ? request.getMaxResults() : 5)
            .collect(Collectors.toList());
    }
    
    /**
     * Bayesian Average - evită bias-ul pentru instituții cu puține review-uri
     * Formula: (C × m + Σx) / (C + n)
     * C = confidence threshold, m = global average, n = number of reviews, Σx = sum of ratings
     */
    private double calculateBayesianAverage(double averageRating, int totalReviews) {
        double sumOfRatings = averageRating * totalReviews;
        return (CONFIDENCE_THRESHOLD * GLOBAL_AVERAGE + sumOfRatings) / (CONFIDENCE_THRESHOLD + totalReviews);
    }
    
    /**
     * Generează rating-uri simulate pentru demo
     * În producție, acestea ar veni din tabelul de feedback
     */
    private RatingData generateSimulatedRating(Long institutionId) {
        Random random = new Random(institutionId); // Seed pentru consistență
        double rating = 2.5 + (random.nextDouble() * 2.5); // 2.5 - 5.0
        int reviews = 5 + random.nextInt(200); // 5 - 205 recenzii
        return new RatingData(Math.round(rating * 10) / 10.0, reviews);
    }
    
    private record RatingData(double averageRating, int totalReviews) {}
    
    @Override
    public String getStrategyName() {
        return "BEST_RATED";
    }
    
    @Override
    public String getDescription() {
        return "Recomandă instituțiile cu cel mai bun rating de la utilizatori";
    }
}
