package edu.saspsproject.controller;

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
    public ResponseEntity<?> saveAppointment(@RequestBody Object appointmentRequest) {
       throw new UnsupportedOperationException("Not yet implemented");
    }


    @GetMapping("/availability")
    public ResponseEntity<?> getAvailability(@RequestParam Long institutionId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
