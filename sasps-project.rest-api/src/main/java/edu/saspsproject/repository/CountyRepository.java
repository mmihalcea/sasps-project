package edu.saspsproject.repository;

import edu.saspsproject.model.County;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountyRepository extends JpaRepository<County,Long> {

    public Optional<County> findByName(String name);
}
