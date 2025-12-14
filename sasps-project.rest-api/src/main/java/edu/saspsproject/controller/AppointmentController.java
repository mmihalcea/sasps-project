package edu.saspsproject.controller;

import edu.saspsproject.dto.response.CountyResponse;
import edu.saspsproject.repository.AppointmentRepository;
import edu.saspsproject.dto.request.AppointmentRequest;
import edu.saspsproject.dto.response.AvailabilityResponse;
import edu.saspsproject.service.AppointmentService;
import edu.saspsproject.model.Appointment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
    public ResponseEntity<AvailabilityResponse> getAvailability(@RequestParam Long institutionId, @RequestParam @DateTimeFormat(pattern = "ddMMyyyy") LocalDate startDate) {
        try {
            AvailabilityResponse availability = appointmentService.getAvailability(institutionId, Optional.ofNullable(startDate));
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

    // BASELINE PROBLEM: Export appointments to CSV - NO separation of concerns
    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportAppointmentsToCSV() {
        try {
            List<Appointment> appointments = repo.findAll();
            
            // CSV generation hardcoded in controller - poor design!
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(outputStream);
            
            // Write header
            writer.println("ID,Title,Institution,Service Type,Date Time,Status,Priority Level,Duration");
            
            // Write data - duplicated logic from multiple places
            for (Appointment app : appointments) {
                writer.printf("%d,%s,%s,%s,%s,%s,%s,%s%n",
                    app.getId(),
                    app.getTitle(),
                    app.getInstitution().getName(),
                    app.getServiceType(),
                    app.getAppointmentTime(),
                    app.getStatus(),
                    app.getPriorityLevel(),
                    app.getEstimatedDuration()
                );
            }
            
            writer.flush();
            byte[] csvData = outputStream.toByteArray();
            
            // Also send email notification - tight coupling!
            sendEmailNotification("admin@test.com", "CSV Export", "Exported " + appointments.size() + " appointments");
            
            log.info("Exported {} appointments to CSV", appointments.size());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf("text/csv"));
            headers.setContentDispositionFormData("attachment", "appointments.csv");
            
            return ResponseEntity.ok().headers(headers).body(csvData);
        } catch (Exception e) {
            log.error("Error exporting appointments: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // BASELINE PROBLEM: Email notification hardcoded in controller - tight coupling!
    private void sendEmailNotification(String to, String subject, String message) {
        try {
            // Hardcoded email sending - no interface, no abstraction
            log.info("Sending email to {} - Subject: {} - Message: {}", to, subject, message);
            
            // In real world, this would be hardcoded SMTP logic here too
            // Demonstrates tight coupling and code duplication
            
            // Simulate sending (in real app, would be SMTP client directly here)
            if (!to.contains("@")) {
                throw new RuntimeException("Invalid email");
            }
            log.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Error sending email: {}", e.getMessage());
            // Silently fail - poor error handling
        }
    }

    // BASELINE PROBLEM: PDF Export - duplicate logic similar to CSV export
    // HARDCODED PDF generation directly in controller - no separation of concerns!
    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportAppointmentsToPDF() {
        try {
            List<Appointment> appointments = repo.findAll();
            
            // Hardcoded PDF generation using PDFBox - all logic in controller!
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            
            // Create PDF document - hardcoded page setup
            org.apache.pdfbox.pdmodel.PDDocument document = new org.apache.pdfbox.pdmodel.PDDocument();
            org.apache.pdfbox.pdmodel.PDPage page = new org.apache.pdfbox.pdmodel.PDPage();
            document.addPage(page);
            
            // Hardcoded content stream
            org.apache.pdfbox.pdmodel.PDPageContentStream contentStream = 
                new org.apache.pdfbox.pdmodel.PDPageContentStream(document, page);
            
            // Hardcoded font and position
            contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 16);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Appointments Report");
            contentStream.endText();
            
            // Hardcoded subtitle
            contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 10);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 730);
            contentStream.showText("Generated: " + java.time.LocalDateTime.now().toString());
            contentStream.endText();
            
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 715);
            contentStream.showText("Total appointments: " + appointments.size());
            contentStream.endText();
            
            // Hardcoded table header
            float yPosition = 690;
            contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 9);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("ID");
            contentStream.newLineAtOffset(30, 0);
            contentStream.showText("Title");
            contentStream.newLineAtOffset(120, 0);
            contentStream.showText("Institution");
            contentStream.newLineAtOffset(100, 0);
            contentStream.showText("Date");
            contentStream.newLineAtOffset(80, 0);
            contentStream.showText("Status");
            contentStream.endText();
            
            // Hardcoded line separator
            yPosition -= 15;
            contentStream.moveTo(50, yPosition);
            contentStream.lineTo(550, yPosition);
            contentStream.stroke();
            
            // Hardcoded data rows - manual loop without abstraction
            yPosition -= 15;
            contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 8);
            
            for (Appointment app : appointments) {
                if (yPosition < 50) {
                    // Hardcoded page break logic
                    contentStream.close();
                    page = new org.apache.pdfbox.pdmodel.PDPage();
                    document.addPage(page);
                    contentStream = new org.apache.pdfbox.pdmodel.PDPageContentStream(document, page);
                    contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 8);
                    yPosition = 750;
                }
                
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText(String.valueOf(app.getId()));
                contentStream.newLineAtOffset(30, 0);
                String title = app.getTitle().length() > 15 ? app.getTitle().substring(0, 15) + "..." : app.getTitle();
                contentStream.showText(title);
                contentStream.newLineAtOffset(120, 0);
                String inst = app.getInstitution().getName().length() > 12 ? app.getInstitution().getName().substring(0, 12) + "..." : app.getInstitution().getName();
                contentStream.showText(inst);
                contentStream.newLineAtOffset(100, 0);
                contentStream.showText(app.getAppointmentTime().toString().substring(0, 16));
                contentStream.newLineAtOffset(80, 0);
                contentStream.showText(app.getStatus().toString());
                contentStream.endText();
                
                yPosition -= 12;
            }
            
            contentStream.close();
            document.save(outputStream);
            document.close();
            
            byte[] pdfData = outputStream.toByteArray();
            
            // Hardcoded email notification - tight coupling!
            sendEmailNotification("admin@test.com", "PDF Export", "Exported " + appointments.size() + " appointments to PDF");
            
            log.info("Exported {} appointments to PDF", appointments.size());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "appointments.pdf");
            
            return ResponseEntity.ok().headers(headers).body(pdfData);
        } catch (Exception e) {
            log.error("Error exporting appointments to PDF: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Helper method to escape HTML special characters
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}
