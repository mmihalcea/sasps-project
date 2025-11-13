package edu.saspsproject.service;

import edu.saspsproject.model.Appointment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {
    public void sendConfirmation(Appointment appointment) {
        if (appointment.getCustomerEmail() != null) {
            log.info("Sending confirmation to {} for appointment {} at {}",
                    appointment.getCustomerEmail(),
                    appointment.getId(),
                    appointment.getAppointmentTime());
        } else {
            log.info("No email provided for appointment {} at {}", appointment.getId(), appointment.getAppointmentTime());
        }
    }
}