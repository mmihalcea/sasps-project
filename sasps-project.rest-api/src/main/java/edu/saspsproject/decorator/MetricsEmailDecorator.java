package edu.saspsproject.decorator;

import edu.saspsproject.adapter.EmailProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * DECORATOR PATTERN - Metrics Email Decorator
 * 
 * Colectează metrici despre operațiunile de email pentru monitorizare.
 * 
 * Metrici colectate:
 * - Număr total de email-uri trimise
 * - Număr de email-uri reușite/eșuate
 * - Timp mediu de trimitere
 * - Distribuție per provider
 * 
 * Utilizare:
 * <pre>
 * MetricsEmailDecorator metrics = new MetricsEmailDecorator(provider);
 * // ... trimite email-uri ...
 * Map<String, Object> stats = metrics.getMetrics();
 * </pre>
 */
@Slf4j
public class MetricsEmailDecorator extends EmailProviderDecorator {
    
    private final AtomicLong totalAttempts = new AtomicLong(0);
    private final AtomicLong successCount = new AtomicLong(0);
    private final AtomicLong failureCount = new AtomicLong(0);
    private final AtomicLong totalDurationMs = new AtomicLong(0);
    
    public MetricsEmailDecorator(EmailProvider wrapped) {
        super(wrapped);
    }
    
    @Override
    public boolean sendEmail(String to, String subject, String content) {
        totalAttempts.incrementAndGet();
        long startTime = System.currentTimeMillis();
        
        boolean result;
        try {
            result = super.sendEmail(to, subject, content);
        } catch (Exception e) {
            failureCount.incrementAndGet();
            long duration = System.currentTimeMillis() - startTime;
            totalDurationMs.addAndGet(duration);
            throw e;
        }
        
        long duration = System.currentTimeMillis() - startTime;
        totalDurationMs.addAndGet(duration);
        
        if (result) {
            successCount.incrementAndGet();
        } else {
            failureCount.incrementAndGet();
        }
        
        // Log periodic metrics (la fiecare 100 de email-uri)
        if (totalAttempts.get() % 100 == 0) {
            log.info("📊 Email Metrics: total={}, success={}, failed={}, avgTime={}ms",
                    totalAttempts.get(),
                    successCount.get(),
                    failureCount.get(),
                    getAverageDuration());
        }
        
        return result;
    }
    
    @Override
    public String getProviderName() {
        return "Metrics[" + wrapped.getProviderName() + "]";
    }
    
    /**
     * Returnează metricile colectate.
     * 
     * @return map cu metrici
     */
    public Map<String, Object> getMetrics() {
        return new ConcurrentHashMap<>(Map.of(
                "provider", wrapped.getProviderName(),
                "totalAttempts", totalAttempts.get(),
                "successCount", successCount.get(),
                "failureCount", failureCount.get(),
                "successRate", getSuccessRate(),
                "averageDurationMs", getAverageDuration(),
                "totalDurationMs", totalDurationMs.get()
        ));
    }
    
    /**
     * Calculează rata de succes.
     */
    public double getSuccessRate() {
        long total = totalAttempts.get();
        if (total == 0) return 0.0;
        return (double) successCount.get() / total * 100;
    }
    
    /**
     * Calculează durata medie.
     */
    public long getAverageDuration() {
        long total = totalAttempts.get();
        if (total == 0) return 0;
        return totalDurationMs.get() / total;
    }
    
    /**
     * Resetează metricile.
     */
    public void resetMetrics() {
        totalAttempts.set(0);
        successCount.set(0);
        failureCount.set(0);
        totalDurationMs.set(0);
        log.info("📊 Metrici resetate");
    }
}
