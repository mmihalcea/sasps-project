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
import java.util.stream.Collectors;

/**
 * STRATEGY PATTERN - Implementare: Recomandare bazată pe distanță
 * 
 * Recomandă instituțiile cele mai apropiate geografic de utilizator.
 * Folosește formula Haversine pentru calculul distanței.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NearestLocationStrategy implements RecommendationStrategy {
    
    private final InstitutionRepository institutionRepository;
    
    // Coordonate default pentru fiecare județ (capitala de județ)
    private static final java.util.Map<String, double[]> COUNTY_COORDINATES = java.util.Map.ofEntries(
        java.util.Map.entry("ALBA", new double[]{46.0677, 23.5700}),
        java.util.Map.entry("ARAD", new double[]{46.1667, 21.3167}),
        java.util.Map.entry("ARGES", new double[]{44.8564, 24.8691}),
        java.util.Map.entry("BACAU", new double[]{46.5670, 26.9146}),
        java.util.Map.entry("BIHOR", new double[]{47.0458, 21.9183}),
        java.util.Map.entry("BISTRITA-NASAUD", new double[]{47.1325, 24.4933}),
        java.util.Map.entry("BOTOSANI", new double[]{47.7487, 26.6695}),
        java.util.Map.entry("BRAILA", new double[]{45.2692, 27.9575}),
        java.util.Map.entry("BRASOV", new double[]{45.6550, 25.6012}),
        java.util.Map.entry("BUCURESTI", new double[]{44.4268, 26.1025}),
        java.util.Map.entry("BUZAU", new double[]{45.1500, 26.8333}),
        java.util.Map.entry("CALARASI", new double[]{44.2000, 27.0333}),
        java.util.Map.entry("CARAS-SEVERIN", new double[]{45.3000, 21.8833}),
        java.util.Map.entry("CLUJ", new double[]{46.7712, 23.6236}),
        java.util.Map.entry("CONSTANTA", new double[]{44.1733, 28.6383}),
        java.util.Map.entry("COVASNA", new double[]{45.8500, 26.1833}),
        java.util.Map.entry("DAMBOVITA", new double[]{44.9257, 25.4572}),
        java.util.Map.entry("DOLJ", new double[]{44.3167, 23.8000}),
        java.util.Map.entry("GALATI", new double[]{45.4353, 28.0080}),
        java.util.Map.entry("GIURGIU", new double[]{43.9037, 25.9699}),
        java.util.Map.entry("GORJ", new double[]{45.0500, 23.2833}),
        java.util.Map.entry("HARGHITA", new double[]{46.3500, 25.8000}),
        java.util.Map.entry("HUNEDOARA", new double[]{45.7500, 22.9000}),
        java.util.Map.entry("IALOMITA", new double[]{44.5667, 27.3667}),
        java.util.Map.entry("IASI", new double[]{47.1585, 27.6014}),
        java.util.Map.entry("ILFOV", new double[]{44.4500, 26.0833}),
        java.util.Map.entry("MARAMURES", new double[]{47.6567, 23.5850}),
        java.util.Map.entry("MEHEDINTI", new double[]{44.6333, 22.6500}),
        java.util.Map.entry("MURES", new double[]{46.5500, 24.5667}),
        java.util.Map.entry("NEAMT", new double[]{46.9167, 26.3833}),
        java.util.Map.entry("OLT", new double[]{44.4333, 24.3667}),
        java.util.Map.entry("PRAHOVA", new double[]{44.9500, 26.0167}),
        java.util.Map.entry("SALAJ", new double[]{47.1833, 23.0500}),
        java.util.Map.entry("SATU-MARE", new double[]{47.7833, 22.8833}),
        java.util.Map.entry("SIBIU", new double[]{45.7928, 24.1519}),
        java.util.Map.entry("SUCEAVA", new double[]{47.6514, 26.2556}),
        java.util.Map.entry("TELEORMAN", new double[]{43.9833, 25.3333}),
        java.util.Map.entry("TIMIS", new double[]{45.7537, 21.2257}),
        java.util.Map.entry("TULCEA", new double[]{45.1667, 28.8000}),
        java.util.Map.entry("VALCEA", new double[]{45.1000, 24.3667}),
        java.util.Map.entry("VASLUI", new double[]{46.6333, 27.7333}),
        java.util.Map.entry("VRANCEA", new double[]{45.7000, 27.1833})
    );
    
    @Override
    public List<InstitutionRecommendation> recommend(RecommendationRequest request) {
        log.info("🗺️ NearestLocationStrategy: Calculez distanțe pentru județul {}", request.getUserCounty());
        
        double[] userCoords = COUNTY_COORDINATES.getOrDefault(
            request.getUserCounty().toUpperCase(), 
            new double[]{44.4268, 26.1025} // Default: București
        );
        
        List<Institution> institutions = institutionRepository.findByServiceType(request.getServiceType());
        
        return institutions.stream()
            .map(inst -> {
                double[] instCoords = COUNTY_COORDINATES.getOrDefault(
                    inst.getCounty().getName().toUpperCase(),
                    userCoords
                );
                double distance = calculateHaversineDistance(
                    userCoords[0], userCoords[1],
                    instCoords[0], instCoords[1]
                );
                
                // Scorul e invers proporțional cu distanța (mai aproape = scor mai mare)
                double score = Math.max(0, 100 - (distance / 5)); // -1 punct per 5km
                
                return InstitutionRecommendation.builder()
                    .institutionId(inst.getId())
                    .institutionName(inst.getName())
                    .county(inst.getCounty().getName())
                    .address(inst.getAddress())
                    .score(score)
                    .distance(distance)
                    .reason(String.format("La %.1f km distanță", distance))
                    .build();
            })
            .sorted(Comparator.comparingDouble(InstitutionRecommendation::getDistance))
            .limit(request.getMaxResults() != null ? request.getMaxResults() : 5)
            .collect(Collectors.toList());
    }
    
    /**
     * Formula Haversine pentru calculul distanței între două puncte geografice
     */
    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Raza Pământului în km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    @Override
    public String getStrategyName() {
        return "NEAREST_LOCATION";
    }
    
    @Override
    public String getDescription() {
        return "Recomandă instituțiile cele mai apropiate geografic";
    }
}
