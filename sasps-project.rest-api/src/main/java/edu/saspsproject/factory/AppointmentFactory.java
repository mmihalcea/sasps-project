package edu.saspsproject.factory;

import edu.saspsproject.dto.request.AppointmentRequest;
import edu.saspsproject.model.Appointment;

public interface AppointmentFactory {
    Appointment.ServiceType supports();
    Appointment create(AppointmentRequest request, Long userId);
}


