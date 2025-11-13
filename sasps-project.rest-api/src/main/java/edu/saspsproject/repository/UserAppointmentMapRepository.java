package edu.saspsproject.repository;

import edu.saspsproject.model.UserAppointmentMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAppointmentMapRepository extends JpaRepository<UserAppointmentMap, Long> {

    List<UserAppointmentMap> findByUserId(Long userId);

    List<UserAppointmentMap> findByAppointmentId(Long appointmentId);
}