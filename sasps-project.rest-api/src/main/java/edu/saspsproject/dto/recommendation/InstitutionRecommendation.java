package edu.saspsproject.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pentru o instituție recomandată
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionRecommendation {
    
    /**
     * ID-ul instituției
     */
    private Long institutionId;
    
    /**
     * Numele instituției
     */
    private String institutionName;
    
    /**
     * Județul instituției
     */
    private String county;
    
    /**
     * Adresa instituției
     */
    private String address;
    
    /**
     * Scorul de recomandare (0-100)
     */
    private double score;
    
    /**
     * Motivul/explicația recomandării
     */
    private String reason;
    
    /**
     * Ranking-ul în listă (1 = cel mai bun)
     */
    private int rank;
    
    // Câmpuri specifice pentru strategii diferite
    
    /**
     * Distanța în km (pentru NEAREST_LOCATION)
     */
    private double distance;
    
    /**
     * Următorul slot disponibil (pentru FASTEST_AVAILABILITY)
     */
    private LocalDateTime nextAvailableSlot;
    
    /**
     * Ore până la disponibilitate (pentru FASTEST_AVAILABILITY)
     */
    private int waitTimeHours;
    
    /**
     * Rating-ul instituției (pentru BEST_RATED)
     */
    private double rating;
    
    /**
     * Numărul de recenzii (pentru BEST_RATED)
     */
    private int totalReviews;
    
    /**
     * Gradul de ocupare în % (pentru LEAST_BUSY)
     */
    private double occupancyRate;
    
    /**
     * Numărul de sloturi disponibile (pentru LEAST_BUSY)
     */
    private int availableSlots;
    
    /**
     * Dacă instituția a primit boost de scor
     */
    private boolean boosted;
    
    /**
     * Motivul boost-ului
     */
    private String boostReason;
}
