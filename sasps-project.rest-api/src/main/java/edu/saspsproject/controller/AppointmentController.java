package edu.saspsproject.controller;

import edu.saspsproject.dto.response.CountyResponse;
import edu.saspsproject.dto.response.InstitutionDetailResponse;
import edu.saspsproject.repository.AppointmentRepository;
import edu.saspsproject.dto.request.AppointmentRequest;
import edu.saspsproject.dto.response.AvailabilityResponse;
import edu.saspsproject.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("/api/appointment")
@RestController
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentRepository repo;

    @PostMapping()
    public ResponseEntity<Long> saveAppointment(@RequestBody AppointmentRequest appointmentRequest) {
        try {
            Long appointmentId = appointmentService.saveAppointment(appointmentRequest);
            log.info("Created appointment with ID: {}", appointmentId);
            return ResponseEntity.ok(appointmentId);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Error creating appointment: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/availability")
    public ResponseEntity<AvailabilityResponse> getAvailability(@RequestParam Long institutionId) {
        try {
            AvailabilityResponse availability = appointmentService.getAvailability(institutionId);
            log.info("Retrieved {} available slots for institution {}",
                    availability.getAvailableSlots().size(), institutionId);
            return ResponseEntity.ok(availability);
        } catch (Exception e) {
            log.error("Error getting availability: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/customer/{email}")
    public ResponseEntity<?> getCustomerAppointments(@PathVariable String email) {
        try {
            var appointments = appointmentService.getCustomerAppointments(email);
            log.info("Retrieved {} appointments for customer {}", appointments.size(), email);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            log.error("Error getting customer appointments: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/institutions")
    public ResponseEntity<?> getAllInstitutions() {
        try {
            var institutions = appointmentService.getAllInstitutions();
            return ResponseEntity.ok(institutions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/institutions/{countyId}")
    public ResponseEntity<?> getInstitutionsByCounty(@PathVariable Long countyId) {
        try {
            var institutions = appointmentService.getInstitutionsByCounty(countyId);
            return ResponseEntity.ok(institutions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }



    @GetMapping("/all")
    public ResponseEntity<?> getAllAppointments() {
        try {
            var appointments = appointmentService.getAllAppointments();
            log.info("Retrieved {} total appointments", appointments.size());
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            log.error("Error getting all appointments: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(@RequestParam(required = false) Long institutionId) {
        try {
            if (institutionId != null) {
                var stats = appointmentService.getInstitutionStats(institutionId);
                return ResponseEntity.ok(stats);
            } else {
                var stats = appointmentService.getGlobalStats();
                return ResponseEntity.ok(stats);
            }
        } catch (Exception e) {
            log.error("Error getting stats: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/by-date")
    public ResponseEntity<?> getAppointmentsByDate(@RequestParam String date) {
        try {
            var appointments = appointmentService.getAppointmentsByDate(date);
            log.info("Retrieved {} appointments for date {}", appointments.size(), date);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            log.error("Error getting appointments by date: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/by-service")
    public ResponseEntity<?> getAppointmentsByService(@RequestParam String serviceType) {
        try {
            var appointments = appointmentService.getAppointmentsByService(serviceType);
            log.info("Retrieved {} appointments for service {}", appointments.size(), serviceType);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            log.error("Error getting appointments by service: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/counties")
    public ResponseEntity<List<CountyResponse>> getAllCounties() {
        try {
            var counties = appointmentService.getAllCounties();
            return ResponseEntity.ok(counties);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Cancel appointment with notification
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        try {
            String reason = body != null ? body.get("reason") : null;
            appointmentService.cancelAppointment(id, reason);
            log.info("Cancelled appointment {}", id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error cancelling appointment: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    // Confirm appointment with notification
    @PostMapping("/{id}/confirm")
    public ResponseEntity<Void> confirmAppointment(@PathVariable Long id) {
        try {
            appointmentService.confirmAppointment(id);
            log.info("Confirmed appointment {}", id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error confirming appointment: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    // Complete appointment
    @PostMapping("/{id}/complete")
    public ResponseEntity<Void> completeAppointment(@PathVariable Long id) {
        try {
            appointmentService.completeAppointment(id);
            log.info("Completed appointment {}", id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error completing appointment: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

}
