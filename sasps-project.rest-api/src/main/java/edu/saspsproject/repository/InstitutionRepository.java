package edu.saspsproject.repository;

import edu.saspsproject.model.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InstitutionRepository extends JpaRepository<Institution, Long> {

    List<Institution> findByType(Institution.InstitutionType type);
    List<Institution> findByCountyIdOrCountyIdIsNull(Long countyId);
    
    /**
     * Găsește instituții care oferă un anumit tip de serviciu
     */
    @Query("SELECT DISTINCT i FROM Institution i JOIN i.availableServices s WHERE s.name LIKE %:serviceType%")
    List<Institution> findByServiceType(@Param("serviceType") String serviceType);
}
