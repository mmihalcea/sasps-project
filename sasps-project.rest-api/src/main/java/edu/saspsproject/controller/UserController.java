package edu.saspsproject.controller;

import edu.saspsproject.model.Appointment;
import edu.saspsproject.model.User;
import edu.saspsproject.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * User controller with many endpoints - no proper REST design
 * Tightly coupled to service layer
 */
@RequestMapping("/api/user")
@RestController
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Get all users - no pagination
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get user by email - exposing email in URL (not good practice)
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        try {
            return ResponseEntity.ok(userService.getUserByEmail(email));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Create user - basic validation only
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            User created = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update user - partial updates supported but poorly handled
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            User updated = userService.updateUser(id, user);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Delete user - cascade deletion
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Deactivate user instead of delete
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        try {
            userService.deactivateUser(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get user statistics
    @GetMapping("/{id}/statistics")
    public ResponseEntity<Map<String, Object>> getUserStatistics(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getUserStatistics(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get user appointment history
    @GetMapping("/{id}/appointments")
    public ResponseEntity<List<Appointment>> getUserAppointments(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getUserAppointmentHistory(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get active users only
    @GetMapping("/active")
    public ResponseEntity<List<User>> getActiveUsers() {
        return ResponseEntity.ok(userService.getActiveUsers());
    }

    // Get users by county
    @GetMapping("/county/{county}")
    public ResponseEntity<List<User>> getUsersByCounty(@PathVariable String county) {
        return ResponseEntity.ok(userService.getUsersByCounty(county));
    }

    // Get users by city
    @GetMapping("/city/{city}")
    public ResponseEntity<List<User>> getUsersByCity(@PathVariable String city) {
        return ResponseEntity.ok(userService.getUsersByCity(city));
    }

    // Update notification preferences
    @PatchMapping("/{id}/notifications")
    public ResponseEntity<User> updateNotificationPreferences(
            @PathVariable Long id,
            @RequestParam(required = false) Boolean emailEnabled,
            @RequestParam(required = false) Boolean smsEnabled,
            @RequestParam(required = false) Integer reminderHours) {
        try {
            User updated = userService.updateNotificationPreferences(id, emailEnabled, smsEnabled, reminderHours);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Send county-wide announcement
    @PostMapping("/county/{county}/announce")
    public ResponseEntity<Void> sendCountyAnnouncement(
            @PathVariable String county,
            @RequestBody Map<String, String> body) {
        try {
            String message = body.get("message");
            if (message == null || message.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            userService.sendCountyAnnouncement(county, message);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}