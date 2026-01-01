package edu.saspsproject.factory;

import edu.saspsproject.dto.request.AppointmentRequest;
import edu.saspsproject.model.Appointment;
import org.springframework.stereotype.Component;

@Component
public class CertificatNastereAppointmentFactory extends BaseAppointmentFactory {

    @Override
    public Appointment.ServiceType supports() {
        return Appointment.ServiceType.CERTIFICAT_NASTERE;
    }

    @Override
    public Appointment create(AppointmentRequest request, Long userId) {
        Appointment a = base(request, userId);
        a.setServiceType(Appointment.ServiceType.CERTIFICAT_NASTERE);
        double duration = applyPriorityAdjustment(
                20.0,
                a.getPriorityLevel()
        );
        a.setEstimatedDuration(duration);

        return a;
    }
}
