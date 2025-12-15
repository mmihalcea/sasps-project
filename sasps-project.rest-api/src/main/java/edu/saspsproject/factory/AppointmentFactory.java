package edu.saspsproject.factory;

import edu.saspsproject.dto.request.AppointmentRequest;
import edu.saspsproject.model.Appointment;

public interface AppointmentFactory {
    Appointment create(AppointmentRequest request, Long userId);
}


