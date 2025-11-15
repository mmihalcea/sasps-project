package edu.saspsproject.repository;

import edu.saspsproject.model.Appointment;
import edu.saspsproject.model.Institution;
import edu.saspsproject.model.Service;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
public class InMemoryRepository {

    private final ConcurrentHashMap<Long, Appointment> appointmentStore = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Institution> institutionStore = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Service> serviceStore = new ConcurrentHashMap<>();
    private final AtomicLong appointmentIdGenerator = new AtomicLong(1);
    private final AtomicLong institutionIdGenerator = new AtomicLong(1);
    private final AtomicLong serviceIdGenerator = new AtomicLong(1);

    public InMemoryRepository() {
        initializeData();
    }

    // Hardcoded initialization without Factory pattern
    private void initializeData() {
        // Initialize institutions - hardcoded without Factory
        Institution primaria = new Institution();
        primaria.setId(1L);
        primaria.setName("Primaria Sector 1");
        primaria.setType("PRIMARIA");
        primaria.setAddress("Str. Primaverii 1");
        primaria.setPhone("021-123-4567");
        primaria.setOpeningTime(LocalTime.of(8, 0));
        primaria.setClosingTime(LocalTime.of(16, 0));
        primaria.setAvailableServices(Arrays.asList("ELIBERARE_CI", "CERTIFICAT_NASTERE", "CERTIFICAT_CASATORIE"));
        primaria.setMaxAppointmentsPerDay(50);
        primaria.setAverageServiceTime(30.0);
        primaria.setRequiresDocuments(true);
        primaria.setNotificationPreferences("BOTH");
        institutionStore.put(1L, primaria);

        Institution anaf = new Institution();
        anaf.setId(2L);
        anaf.setName("ANAF Bucuresti");
        anaf.setType("ANAF");
        anaf.setAddress("Calea Victoriei 15");
        anaf.setPhone("021-987-6543");
        anaf.setOpeningTime(LocalTime.of(8, 30));
        anaf.setClosingTime(LocalTime.of(15, 30));
        anaf.setAvailableServices(Arrays.asList("DECLARATIE_FISCALA", "CONSULTANTA_FISCALA", "CERTIFICAT_FISCAL"));
        anaf.setMaxAppointmentsPerDay(40);
        anaf.setAverageServiceTime(45.0);
        anaf.setRequiresDocuments(true);
        anaf.setNotificationPreferences("EMAIL");
        institutionStore.put(2L, anaf);

        Institution anpc = new Institution();
        anpc.setId(3L);
        anpc.setName("ANPC Bucuresti");
        anpc.setType("ANPC");
        anpc.setAddress("Bd. Aviatorilor 72");
        anpc.setPhone("021-456-7890");
        anpc.setOpeningTime(LocalTime.of(9, 0));
        anpc.setClosingTime(LocalTime.of(17, 0));
        anpc.setAvailableServices(Arrays.asList("RECLAMATIE_CONSUMER", "MEDIERE_CONFLICT", "INFORMARE_DREPTURI"));
        anpc.setMaxAppointmentsPerDay(30);
        anpc.setAverageServiceTime(60.0);
        anpc.setRequiresDocuments(false);
        anpc.setNotificationPreferences("SMS");
        institutionStore.put(3L, anpc);

        // Initialize services - hardcoded without Factory
        Service eliberareCI = new Service();
        eliberareCI.setId(1L);
        eliberareCI.setName("Eliberare Carte Identitate");
        eliberareCI.setInstitutionType("PRIMARIA");
        eliberareCI.setEstimatedDuration(30.0);
        eliberareCI.setRequiredDocuments("Certificat nastere, Dovada domiciliu");
        eliberareCI.setFee(7.0);
        eliberareCI.setCategory("DOCUMENTE");
        eliberareCI.setRequiresAppointment(true);
        eliberareCI.setPriorityHandling("NORMAL");
        serviceStore.put(1L, eliberareCI);

        Service declaratieFiscala = new Service();
        declaratieFiscala.setId(2L);
        declaratieFiscala.setName("Depunere Declaratie Fiscala");
        declaratieFiscala.setInstitutionType("ANAF");
        declaratieFiscala.setEstimatedDuration(45.0);
        declaratieFiscala.setRequiredDocuments("Declaratie completata, Anexe");
        declaratieFiscala.setFee(0.0);
        declaratieFiscala.setCategory("FISCAL");
        declaratieFiscala.setRequiresAppointment(true);
        declaratieFiscala.setPriorityHandling("HIGH");
        serviceStore.put(2L, declaratieFiscala);
    }

    // Appointment operations
    public Long save(Appointment appointment) {
        long id = appointmentIdGenerator.getAndIncrement();
        appointment.setId(id);
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());
        
        // Hardcoded business logic without Strategy pattern
        if ("URGENT".equals(appointment.getPriorityLevel())) {
            appointment.setStatus("CONFIRMED");
        } else {
            appointment.setStatus("PENDING");
        }
        
        appointment.setReminderSent(false);
        appointmentStore.put(id, appointment);
        return id;
    }

    public List<Appointment> findByInstitutionId(Long institutionId) {
        if (institutionId == null) return new ArrayList<>();
        return appointmentStore.values()
                .stream()
                .filter(a -> institutionId.equals(a.getInstitutionId()))
                .collect(Collectors.toList());
    }

    public List<Appointment> findByCustomerEmail(String email) {
        if (email == null) return new ArrayList<>();
        return appointmentStore.values()
                .stream()
                .filter(a -> email.equals(a.getCustomerEmail()))
                .collect(Collectors.toList());
    }

    public List<Appointment> findByStatus(String status) {
        return appointmentStore.values()
                .stream()
                .filter(a -> status.equals(a.getStatus()))
                .collect(Collectors.toList());
    }

    public List<Appointment> findAll() {
        return new ArrayList<>(appointmentStore.values());
    }

    public Appointment findById(Long id) {
        return appointmentStore.get(id);
    }

    public void updateAppointment(Appointment appointment) {
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentStore.put(appointment.getId(), appointment);
    }

    // Institution operations
    public List<Institution> findAllInstitutions() {
        return new ArrayList<>(institutionStore.values());
    }

    public Institution findInstitutionById(Long id) {
        return institutionStore.get(id);
    }

    // Service operations
    public List<Service> findServicesByInstitutionType(String institutionType) {
        return serviceStore.values()
                .stream()
                .filter(s -> institutionType.equals(s.getInstitutionType()))
                .collect(Collectors.toList());
    }

    public Service findServiceById(Long id) {
        return serviceStore.get(id);
    }

    public List<Service> findAllServices() {
        return new ArrayList<>(serviceStore.values());
    }
}
