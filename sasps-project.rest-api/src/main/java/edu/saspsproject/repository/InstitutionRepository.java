package edu.saspsproject.repository;

import edu.saspsproject.model.Institution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstitutionRepository extends JpaRepository<Institution, Long> {}
