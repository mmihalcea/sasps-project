package edu.saspsproject.repository;

import edu.saspsproject.model.Appointment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
public class InMemoryRepository {

    // Renamed class to match filename

    private final ConcurrentHashMap<Long, Appointment> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Long save(Appointment appointment) {
        long id = idGenerator.getAndIncrement();
        appointment.setId(id);
        store.put(id, appointment);
        return id;
    }

    public List<Appointment> findByInstitutionId(Long institutionId) {
        if (institutionId == null) return new ArrayList<>();
        return store.values()
                .stream()
                .filter(a -> institutionId.equals(a.getInstitutionId()))
                .collect(Collectors.toList());
    }

    public List<Appointment> findAll() {
        return new ArrayList<>(store.values());
    }
}
