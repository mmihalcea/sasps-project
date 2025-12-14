package edu.saspsproject.service;

import edu.saspsproject.model.Appointment;
import edu.saspsproject.model.User;
import edu.saspsproject.repository.AppointmentRepository;
import edu.saspsproject.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User service without design patterns, monolithic service with tight coupling
 * No facade pattern, no single responsibility principle
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    public UserService(UserRepository userRepository,
                      AppointmentRepository appointmentRepository,
                      EmailService emailService,
                      NotificationService notificationService) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    // Create user with notification, tightly coupled
    public User createUser(User user) {
        // Validation logic directly in service, no validator pattern
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Set defaults, no builder pattern
        if (user.getEmailNotificationsEnabled() == null) {
            user.setEmailNotificationsEnabled(true);
        }
        if (user.getSmsNotificationsEnabled() == null) {
            user.setSmsNotificationsEnabled(false);
        }
        if (user.getReminderHoursBefore() == null) {
            user.setReminderHoursBefore(24);
        }
        if (user.getPreferredLanguage() == null) {
            user.setPreferredLanguage("ro");
        }
        if (user.getActive() == null) {
            user.setActive(true);
        }

        User savedUser = userRepository.save(user);

        // Directly call email service, no event system
        try {
            emailService.sendWelcomeEmail(savedUser);
        } catch (Exception e) {
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }

        return savedUser;
    }

    // Update user with notifications, duplicated validation
    public User updateUser(Long userId, User updatedUser) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Hardcoded field updates, no mapper pattern
        if (updatedUser.getName() != null) {
            existingUser.setName(updatedUser.getName());
        }
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.findByEmail(updatedUser.getEmail()).isPresent()) {
                throw new RuntimeException("Email already exists");
            }
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPhone() != null) {
            existingUser.setPhone(updatedUser.getPhone());
        }
        if (updatedUser.getAddress() != null) {
            existingUser.setAddress(updatedUser.getAddress());
        }
        if (updatedUser.getCity() != null) {
            existingUser.setCity(updatedUser.getCity());
        }
        if (updatedUser.getCounty() != null) {
            existingUser.setCounty(updatedUser.getCounty());
        }
        if (updatedUser.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(updatedUser.getDateOfBirth());
        }
        if (updatedUser.getEmailNotificationsEnabled() != null) {
            existingUser.setEmailNotificationsEnabled(updatedUser.getEmailNotificationsEnabled());
        }
        if (updatedUser.getSmsNotificationsEnabled() != null) {
            existingUser.setSmsNotificationsEnabled(updatedUser.getSmsNotificationsEnabled());
        }
        if (updatedUser.getReminderHoursBefore() != null) {
            existingUser.setReminderHoursBefore(updatedUser.getReminderHoursBefore());
        }
        if (updatedUser.getPreferredLanguage() != null) {
            existingUser.setPreferredLanguage(updatedUser.getPreferredLanguage());
        }

        return userRepository.save(existingUser);
    }

    // Get user by email
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    // Get user by ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    // Get all users, no pagination
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get active users only
    public List<User> getActiveUsers() {
        return userRepository.findByActive(true);
    }

    // Deactivate user, cascading logic hardcoded
    public void deactivateUser(Long userId) {
        User user = getUserById(userId);
        user.setActive(false);
        userRepository.save(user);

        // Hardcoded: also cancel all future appointments
        List<Appointment> futureAppointments = appointmentRepository.findAll().stream()
                .filter(apt -> apt.getUserId().equals(userId))
                .filter(apt -> apt.getAppointmentTime().isAfter(LocalDateTime.now()))
                .filter(apt -> apt.getStatus() != Appointment.Status.CANCELLED)
                .toList();

        for (Appointment apt : futureAppointments) {
            apt.setStatus(Appointment.Status.CANCELLED);
            appointmentRepository.save(apt);
            
            // Send cancellation notification, tightly coupled
            try {
                notificationService.createNotification(
                    userId, 
                    "Programarea a fost anulată deoarece contul a fost dezactivat", 
                    "CANCELLATION"
                );
            } catch (Exception e) {
                System.err.println("Failed to send cancellation notification");
            }
        }
    }

    // Complex user statistics, no separate analytics service
    public Map<String, Object> getUserStatistics(Long userId) {
        User user = getUserById(userId);
        List<Appointment> allAppointments = appointmentRepository.findAll().stream()
                .filter(apt -> apt.getUserId().equals(userId))
                .toList();

        Map<String, Object> stats = new HashMap<>();
        stats.put("userId", userId);
        stats.put("userName", user.getName());
        stats.put("email", user.getEmail());
        stats.put("totalAppointments", allAppointments.size());

        // Count by status, hardcoded logic
        long pending = allAppointments.stream()
                .filter(apt -> apt.getStatus() == Appointment.Status.PENDING).count();
        long confirmed = allAppointments.stream()
                .filter(apt -> apt.getStatus() == Appointment.Status.CONFIRMED).count();
        long completed = allAppointments.stream()
                .filter(apt -> apt.getStatus() == Appointment.Status.COMPLETED).count();
        long cancelled = allAppointments.stream()
                .filter(apt -> apt.getStatus() == Appointment.Status.CANCELLED).count();

        stats.put("pendingAppointments", pending);
        stats.put("confirmedAppointments", confirmed);
        stats.put("completedAppointments", completed);
        stats.put("cancelledAppointments", cancelled);

        // Future appointments
        long futureAppointments = allAppointments.stream()
                .filter(apt -> apt.getAppointmentTime().isAfter(LocalDateTime.now()))
                .filter(apt -> apt.getStatus() != Appointment.Status.CANCELLED)
                .count();
        stats.put("upcomingAppointments", futureAppointments);

        // Past appointments
        long pastAppointments = allAppointments.stream()
                .filter(apt -> apt.getAppointmentTime().isBefore(LocalDateTime.now()))
                .count();
        stats.put("pastAppointments", pastAppointments);

        return stats;
    }

    // Get users by county, for administrative purposes
    public List<User> getUsersByCounty(String county) {
        return userRepository.findByCounty(county);
    }

    // Get users by city
    public List<User> getUsersByCity(String city) {
        return userRepository.findByCity(city);
    }

    // Bulk notification to all users in a county, no batch processing pattern
    public void sendCountyAnnouncement(String county, String message) {
        List<User> users = getUsersByCounty(county);
        
        for (User user : users) {
            if (user.getActive() && user.getEmailNotificationsEnabled()) {
                try {
                    // Hardcoded notification creation, duplicate code
                    notificationService.createNotification(user.getId(), message, "ANNOUNCEMENT");
                } catch (Exception e) {
                    System.err.println("Failed to send announcement to user: " + user.getId());
                }
            }
        }
        
        System.out.println("Sent announcement to " + users.size() + " users in county: " + county);
    }

    // Update notification preferences
    public User updateNotificationPreferences(Long userId, 
                                             Boolean emailEnabled, 
                                             Boolean smsEnabled, 
                                             Integer reminderHours) {
        User user = getUserById(userId);
        
        if (emailEnabled != null) {
            user.setEmailNotificationsEnabled(emailEnabled);
        }
        if (smsEnabled != null) {
            user.setSmsNotificationsEnabled(smsEnabled);
        }
        if (reminderHours != null) {
            if (reminderHours < 1 || reminderHours > 72) {
                throw new RuntimeException("Reminder hours must be between 1 and 72");
            }
            user.setReminderHoursBefore(reminderHours);
        }
        
        return userRepository.save(user);
    }

    // Get user appointment history, duplicated from AppointmentService
    public List<Appointment> getUserAppointmentHistory(Long userId) {
        return appointmentRepository.findAll().stream()
                .filter(apt -> apt.getUserId().equals(userId))
                .sorted((a1, a2) -> a2.getAppointmentTime().compareTo(a1.getAppointmentTime()))
                .toList();
    }

    // Delete user, cascade delete not handled by DB
    public void deleteUser(Long userId) {
        User user = getUserById(userId);
        
        // Manually delete all users appointments, no cascade
        List<Appointment> userAppointments = appointmentRepository.findAll().stream()
                .filter(apt -> apt.getUserId().equals(userId))
                .toList();
        
        appointmentRepository.deleteAll(userAppointments);
        
        userRepository.delete(user);
        
        System.out.println("Deleted user " + userId + " and " + userAppointments.size() + " appointments");
    }
}
