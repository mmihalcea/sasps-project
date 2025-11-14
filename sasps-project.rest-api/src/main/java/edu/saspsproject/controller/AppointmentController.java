package edu.saspsproject.controller;

import edu.saspsproject.dto.AppointmentRequest;
import edu.saspsproject.dto.AvailabilityResponse;
import edu.saspsproject.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/api/appointment")
@RestController
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;


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

    @GetMapping("/services/{institutionType}")
    public ResponseEntity<?> getServicesByInstitutionType(@PathVariable String institutionType) {
        try {
            var services = appointmentService.getServicesByInstitutionType(institutionType);
            return ResponseEntity.ok(services);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
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

}
