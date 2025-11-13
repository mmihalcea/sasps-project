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

}
