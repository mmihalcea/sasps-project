package edu.saspsproject.controller;

import edu.saspsproject.model.Appointment;
import edu.saspsproject.repository.AppointmentRepository;
import edu.saspsproject.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/api/appointment")
@RestController
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentRepository repo;

    @PostMapping()
    public ResponseEntity<?> saveAppointment(@RequestBody Object appointmentRequest) {
       throw new UnsupportedOperationException("Not yet implemented");
    }

    @GetMapping("/availability")
    public ResponseEntity<?> getAvailability(@RequestParam Long institutionId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @GetMapping
    public List<Appointment> all() {
        return repo.findAll();
    }

    @PostMapping("/create")
    public Appointment create(@RequestBody Appointment a) {
        if (a.getStartTime() == null || a.getEndTime() == null || !a.getStartTime().isBefore(a.getEndTime())) {
            throw new IllegalArgumentException("startTime trebuie sa fie inainte de endTime");
        }
        if (repo.existsByStartTimeLessThanEqualAndEndTimeGreaterThanEqual(a.getEndTime(), a.getStartTime())) {
            throw new IllegalStateException("Programarea se suprapune cu alta programare existenta");
        }
        return repo.save(a);
    }

}
