package edu.saspsproject.repository;

import edu.saspsproject.model.County;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountyRepository extends JpaRepository<County,Long> {
}
