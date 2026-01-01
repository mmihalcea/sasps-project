package edu.saspsproject.factory;

import edu.saspsproject.dto.request.AppointmentRequest;
import edu.saspsproject.model.Appointment;
import org.springframework.stereotype.Component;

@Component
public class PreschimbarePermisAppointmentFactory extends BaseAppointmentFactory {

    @Override
    public Appointment.ServiceType supports() {
        return Appointment.ServiceType.PRESCHIMBARE_PERMIS;
    }

    @Override
    public Appointment create(AppointmentRequest request, Long userId) {
        Appointment a = base(request, userId);
        a.setServiceType(Appointment.ServiceType.PRESCHIMBARE_PERMIS);
        double duration = applyPriorityAdjustment(
                45.0,
                a.getPriorityLevel()
        );
        a.setEstimatedDuration(duration);

        return a;
    }
}
