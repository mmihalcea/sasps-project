package edu.saspsproject;

import edu.saspsproject.dto.request.AppointmentRequest;
import edu.saspsproject.dto.response.AppointmentResponse;
import edu.saspsproject.dto.response.AvailabilityResponse;
import edu.saspsproject.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/api/appointment")
@RestController
@RequiredArgsConstructor
public class AppointmentControllerTests {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponse> saveAppointment(@RequestBody AppointmentRequest appointmentRequest) {
        Long id = appointmentService.saveAppointment(appointmentRequest);
        return ResponseEntity.status(201).body(new AppointmentResponse(id));
    }

    @GetMapping("/availability")
    public ResponseEntity<AvailabilityResponse> getAvailability(@RequestParam Long institutionId, @RequestParam(required = false) String date) {
        AvailabilityResponse resp = appointmentService.getAvailability(institutionId, java.util.Optional.ofNullable(date).map(java.time.LocalDate::parse));
        return ResponseEntity.ok(resp);
    }
}
