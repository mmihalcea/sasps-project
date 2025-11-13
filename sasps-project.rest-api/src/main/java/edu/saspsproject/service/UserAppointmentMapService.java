package edu.saspsproject.service;

import edu.saspsproject.model.UserAppointmentMap;
import edu.saspsproject.repository.AppointmentRepository;
import edu.saspsproject.repository.UserAppointmentMapRepository;
import edu.saspsproject.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserAppointmentMapService {
    private final UserRepository userRepo;
    private final AppointmentRepository appointmentRepo;
    private final UserAppointmentMapRepository userAppointmentRepo;

    public UserAppointmentMapService(UserRepository userRepo,
                                  AppointmentRepository appointmentRepo,
                                  UserAppointmentMapRepository userAppointmentRepo) {
        this.userRepo = userRepo;
        this.appointmentRepo = appointmentRepo;
        this.userAppointmentRepo = userAppointmentRepo;
    }

    public UserAppointmentMap addMapping(Long userId, Long appointmentId) {
        var user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        var appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        var mapping = new UserAppointmentMap(user, appointment);
        return userAppointmentRepo.save(mapping);
    }
}
