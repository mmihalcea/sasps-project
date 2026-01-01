package edu.saspsproject.factory;

import edu.saspsproject.model.Appointment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AppointmentFactoryProvider {

    private final Map<Appointment.ServiceType, AppointmentFactory> map;

    public AppointmentFactoryProvider(List<AppointmentFactory> factories) {
        this.map = factories.stream().collect(Collectors.toMap(
                AppointmentFactory::supports,
                f -> f
        ));
    }

    public AppointmentFactory get(Appointment.ServiceType type) {
        AppointmentFactory f = map.get(type);
        if (f == null) throw new IllegalArgumentException("No factory for service type: " + type);
        return f;
    }
}