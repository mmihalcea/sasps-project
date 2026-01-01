package edu.saspsproject.factory;

import edu.saspsproject.dto.request.AppointmentRequest;
import edu.saspsproject.model.Appointment;
import org.springframework.stereotype.Component;

@Component
public class DeclaratieFiscalaAppointmentFactory extends BaseAppointmentFactory {

    @Override
    public Appointment.ServiceType supports() {
        return Appointment.ServiceType.DECLARATIE_FISCALA;
    }

    @Override
    public Appointment create(AppointmentRequest request, Long userId) {
        Appointment a = base(request, userId);
        a.setServiceType(Appointment.ServiceType.DECLARATIE_FISCALA);

        double duration = applyPriorityAdjustment(
                45.0,
                a.getPriorityLevel()
        );
        a.setEstimatedDuration(duration);

        return a;
    }
}