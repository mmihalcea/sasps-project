package edu.saspsproject.repository;

import edu.saspsproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByActive(Boolean active);
    List<User> findByEmailNotificationsEnabled(Boolean enabled);
    List<User> findByCounty(String county);
    List<User> findByCity(String city);
}