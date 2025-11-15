package edu.saspsproject.repository;

import edu.saspsproject.model.Institution;
import edu.saspsproject.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstitutionRepository extends JpaRepository<Institution, Long> {

    List<Institution> findByType(Institution.InstitutionType type);
}
