package edu.saspsproject.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO pentru cererea de recomandare
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequest {
    
    /**
     * Tipul serviciului dorit (ex: DECLARATIE_FISCALA, CARTE_IDENTITATE)
     */
    private String serviceType;
    
    /**
     * Județul utilizatorului pentru calcul distanță
     */
    private String userCounty;
    
    /**
     * Strategia de recomandare dorită
     * Valori: NEAREST_LOCATION, FASTEST_AVAILABILITY, BEST_RATED, LEAST_BUSY, COMPOSITE
     */
    private String strategy;
    
    /**
     * Numărul maxim de rezultate (default: 5)
     */
    private Integer maxResults;
    
    /**
     * Preferă instituții din același județ
     */
    private boolean preferSameCounty;
    
    /**
     * Distanța maximă acceptabilă (în km)
     */
    private Double maxDistance;
    
    /**
     * Rating minim acceptat (1-5)
     */
    private Double minRating;
    
    /**
     * COMPOSITE PATTERN - Ponderi pentru fiecare strategie (0-100)
     * Ex: {"NEAREST_LOCATION": 40, "BEST_RATED": 30, "FASTEST_AVAILABILITY": 30}
     */
    private Map<String, Integer> strategyWeights;
}
