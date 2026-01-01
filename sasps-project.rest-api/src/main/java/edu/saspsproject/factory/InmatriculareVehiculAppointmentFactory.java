package edu.saspsproject.factory;

import edu.saspsproject.dto.request.AppointmentRequest;
import edu.saspsproject.model.Appointment;
import org.springframework.stereotype.Component;

@Component
public class InmatriculareVehiculAppointmentFactory extends BaseAppointmentFactory {

    @Override
    public Appointment.ServiceType supports() {
        return Appointment.ServiceType.INMATRICULARE_VEHICUL;
    }

    @Override
    public Appointment create(AppointmentRequest request, Long userId) {
        Appointment a = base(request, userId);
        a.setServiceType(Appointment.ServiceType.INMATRICULARE_VEHICUL);
        double duration = applyPriorityAdjustment(
                30.0,
                a.getPriorityLevel()
        );
        a.setEstimatedDuration(duration);

        return a;
    }
}