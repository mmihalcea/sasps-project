package edu.saspsproject.singleton;

import edu.saspsproject.model.Institution;
import edu.saspsproject.model.PublicService;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SINGLETON PATTERN - Institution Cache Manager
 * 
 * Gestionează un cache pentru datele instituțiilor care se schimbă rar.
 * Implementat ca Singleton thread-safe pentru acces global.
 * 
 * Beneficii:
 * - Reduce numărul de query-uri către baza de date
 * - Îmbunătățește performanța aplicației
 * - Acces global la cache din orice punct al aplicației
 * - Thread-safe pentru aplicații concurente
 * 
 * Implementare:
 * - Bill Pugh Singleton (folosind inner static class)
 * - Thread-safe fără synchronized overhead
 * - Lazy initialization
 * 
 * Utilizare:
 * <pre>
 * InstitutionCacheManager cache = InstitutionCacheManager.getInstance();
 * cache.cacheInstitution(institution);
 * Optional<Institution> cached = cache.getInstitution(institutionId);
 * </pre>
 * 
 * Notă: În producție, se recomandă folosirea soluțiilor enterprise
 * precum Redis, Hazelcast sau Spring Cache abstraction.
 */
@Slf4j
public class InstitutionCacheManager {
    
    // Cache pentru instituții
    private final Map<Long, CacheEntry<Institution>> institutionCache;
    
    // Cache pentru servicii publice
    private final Map<Long, CacheEntry<List<PublicService>>> servicesCache;
    
    // Durată implicită de viață a cache-ului (1 oră)
    private static final Duration DEFAULT_TTL = Duration.ofHours(1);
    
    /**
     * Constructor privat - previne instanțierea din exterior.
     */
    private InstitutionCacheManager() {
        log.info("Inițializare InstitutionCacheManager (Singleton)");
        this.institutionCache = new ConcurrentHashMap<>();
        this.servicesCache = new ConcurrentHashMap<>();
    }
    
    /**
     * Bill Pugh Singleton - inner static class pentru lazy initialization.
     * Clasa internă nu este încărcată până când nu este referită.
     */
    private static class SingletonHolder {
        private static final InstitutionCacheManager INSTANCE = new InstitutionCacheManager();
    }
    
    /**
     * Returnează instanța unică a cache manager-ului.
     * Thread-safe prin mecanismul de class loading al JVM.
     * 
     * @return instanța singleton
     */
    public static InstitutionCacheManager getInstance() {
        return SingletonHolder.INSTANCE;
    }
    
    /**
     * Adaugă sau actualizează o instituție în cache.
     * 
     * @param institution instituția de stocat
     */
    public void cacheInstitution(Institution institution) {
        if (institution == null || institution.getId() == null) {
            log.warn("Încercare de a cache-ui instituție invalidă");
            return;
        }
        
        institutionCache.put(institution.getId(), new CacheEntry<>(institution, DEFAULT_TTL));
        log.debug("Instituție cached: {} (ID: {})", institution.getName(), institution.getId());
    }
    
    /**
     * Obține o instituție din cache dacă există și nu a expirat.
     * 
     * @param institutionId ID-ul instituției
     * @return Optional cu instituția sau empty dacă nu există/a expirat
     */
    public Optional<Institution> getInstitution(Long institutionId) {
        CacheEntry<Institution> entry = institutionCache.get(institutionId);
        
        if (entry == null) {
            log.debug("Cache miss pentru instituție ID: {}", institutionId);
            return Optional.empty();
        }
        
        if (entry.isExpired()) {
            log.debug("Cache expirat pentru instituție ID: {}", institutionId);
            institutionCache.remove(institutionId);
            return Optional.empty();
        }
        
        log.debug("Cache hit pentru instituție ID: {}", institutionId);
        return Optional.of(entry.getValue());
    }
    
    /**
     * Cache-uiește lista de servicii pentru o instituție.
     * 
     * @param institutionId ID-ul instituției
     * @param services lista de servicii
     */
    public void cacheServices(Long institutionId, List<PublicService> services) {
        if (institutionId == null || services == null) {
            return;
        }
        
        servicesCache.put(institutionId, new CacheEntry<>(services, DEFAULT_TTL));
        log.debug("Servicii cached pentru instituție ID: {} ({} servicii)", 
                institutionId, services.size());
    }
    
    /**
     * Obține serviciile unei instituții din cache.
     * 
     * @param institutionId ID-ul instituției
     * @return Optional cu lista de servicii
     */
    public Optional<List<PublicService>> getServices(Long institutionId) {
        CacheEntry<List<PublicService>> entry = servicesCache.get(institutionId);
        
        if (entry == null || entry.isExpired()) {
            servicesCache.remove(institutionId);
            return Optional.empty();
        }
        
        return Optional.of(entry.getValue());
    }
    
    /**
     * Invalidează toate intrările din cache.
     */
    public void clearAll() {
        institutionCache.clear();
        servicesCache.clear();
        log.info("Cache complet invalidat");
    }
    
    /**
     * Invalidează cache-ul pentru o instituție specifică.
     * 
     * @param institutionId ID-ul instituției
     */
    public void invalidate(Long institutionId) {
        institutionCache.remove(institutionId);
        servicesCache.remove(institutionId);
        log.debug("Cache invalidat pentru instituție ID: {}", institutionId);
    }
    
    /**
     * Returnează statistici despre cache.
     * 
     * @return map cu statistici
     */
    public Map<String, Object> getStats() {
        long validInstitutions = institutionCache.values().stream()
                .filter(e -> !e.isExpired())
                .count();
        long validServices = servicesCache.values().stream()
                .filter(e -> !e.isExpired())
                .count();
        
        return Map.of(
                "totalInstitutionEntries", institutionCache.size(),
                "validInstitutionEntries", validInstitutions,
                "totalServicesEntries", servicesCache.size(),
                "validServicesEntries", validServices
        );
    }
    
    /**
     * Clasă internă pentru intrări în cache cu TTL.
     */
    private static class CacheEntry<T> {
        private final T value;
        private final LocalDateTime expiresAt;
        
        CacheEntry(T value, Duration ttl) {
            this.value = value;
            this.expiresAt = LocalDateTime.now().plus(ttl);
        }
        
        T getValue() {
            return value;
        }
        
        boolean isExpired() {
            return LocalDateTime.now().isAfter(expiresAt);
        }
    }
}
