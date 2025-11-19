package edu.saspsproject.repository;

import edu.saspsproject.model.PublicService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicServiceRepository extends JpaRepository<PublicService, Long> {
}
