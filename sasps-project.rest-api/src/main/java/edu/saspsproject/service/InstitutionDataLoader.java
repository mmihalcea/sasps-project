package edu.saspsproject.service;

import edu.saspsproject.model.County;
import edu.saspsproject.model.Institution;
import edu.saspsproject.model.PublicService;
import edu.saspsproject.repository.CountyRepository;
import edu.saspsproject.repository.InstitutionRepository;
import edu.saspsproject.repository.PublicServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InstitutionDataLoader {
    
    private final CountyRepository countyRepository;
    private final InstitutionRepository institutionRepository;
    private final PublicServiceRepository publicServiceRepository;

    public void loadSimpleInstitutions() {
        // Create services for town halls
        List<PublicService> townHallServices = createTownHallServices();
        List<PublicService> spclepServices = createSpclepServices();
        List<PublicService> drpcivServices = createDrpcivServices();
        
        // Get all counties and create 2 institutions per county
        List<County> counties = countyRepository.findAll();
        List<Institution> allInstitutions = new ArrayList<>();
        
        for (County county : counties) {
            // Primaria
            Institution primaria = new Institution();
            primaria.setName("Primaria " + county.getName());
            primaria.setCounty(county);
            primaria.setAddress("Str. Principala nr. 1, " + county.getName());
            primaria.setPhone("021-" + String.format("%06d", county.getId() * 1000));
            primaria.setType(Institution.InstitutionType.PRIMARIA);
            primaria.setMaxAppointmentsPerDay(20);
            primaria.setClosingTime(LocalTime.of(16, 0));
            primaria.setAverageServiceTime(15D);
            primaria.setOpeningTime(LocalTime.of(9, 0));
            primaria.setAvailableServices(townHallServices);
            allInstitutions.add(primaria);
            
            // SPCLEP
            Institution spclep = new Institution();
            spclep.setName("SPCLEP " + county.getName());
            spclep.setCounty(county);
            spclep.setAddress("Str. Centrala nr. 10, " + county.getName());
            spclep.setPhone("021-" + String.format("%06d", county.getId() * 1000 + 100));
            spclep.setType(Institution.InstitutionType.SPCLEP);
            spclep.setMaxAppointmentsPerDay(15);
            spclep.setClosingTime(LocalTime.of(15, 0));
            spclep.setAverageServiceTime(20D);
            spclep.setOpeningTime(LocalTime.of(10, 0));
            spclep.setAvailableServices(spclepServices);
            allInstitutions.add(spclep);
        }
        
        // Add DRPCIV - one per country
        Institution drpciv = new Institution();
        drpciv.setName("DRPCIV Bucuresti");
        drpciv.setCounty(countyRepository.findByName("Bucuresti").orElse(counties.get(0)));
        drpciv.setAddress("Bd. Dinicu Golescu nr. 38, Bucuresti");
        drpciv.setPhone("021-3052222");
        drpciv.setType(Institution.InstitutionType.DRPCIV);
        drpciv.setMaxAppointmentsPerDay(40);
        drpciv.setClosingTime(LocalTime.of(17, 0));
        drpciv.setAverageServiceTime(10D);
        drpciv.setOpeningTime(LocalTime.of(8, 0));
        drpciv.setAvailableServices(drpcivServices);
        allInstitutions.add(drpciv);
        
        institutionRepository.saveAll(allInstitutions);
    }
    
    private List<PublicService> createTownHallServices() {
        List<PublicService> services = new ArrayList<>();
        
        PublicService ps1 = new PublicService();
        ps1.setName("Eliberare certificate");
        ps1.setInstitutionType(Institution.InstitutionType.PRIMARIA);
        ps1.setFee(50D);
        ps1.setRequiredDocuments("Buletin");
        ps1.setEstimatedDuration(20D);
        ps1.setDescription("Eliberare certificate (nastere, casatorie, deces)");
        services.add(publicServiceRepository.save(ps1));
        
        PublicService ps2 = new PublicService();
        ps2.setName("Taxe si impozite locale");
        ps2.setInstitutionType(Institution.InstitutionType.PRIMARIA);
        ps2.setFee(0D);
        ps2.setRequiredDocuments("Buletin");
        ps2.setEstimatedDuration(15D);
        ps2.setDescription("Plata taxe si impozite locale");
        services.add(publicServiceRepository.save(ps2));
        
        return services;
    }
    
    private List<PublicService> createSpclepServices() {
        List<PublicService> services = new ArrayList<>();
        
        PublicService ps1 = new PublicService();
        ps1.setName("Eliberare carte de identitate");
        ps1.setInstitutionType(Institution.InstitutionType.SPCLEP);
        ps1.setFee(100D);
        ps1.setRequiredDocuments("Certificat nastere");
        ps1.setEstimatedDuration(30D);
        ps1.setDescription("Eliberare carte de identitate");
        services.add(publicServiceRepository.save(ps1));
        
        return services;
    }
    
    private List<PublicService> createDrpcivServices() {
        List<PublicService> services = new ArrayList<>();
        
        PublicService ps1 = new PublicService();
        ps1.setName("Preschimbare permis de conducere");
        ps1.setInstitutionType(Institution.InstitutionType.DRPCIV);
        ps1.setFee(100D);
        ps1.setRequiredDocuments("Permis vechi");
        ps1.setEstimatedDuration(10D);
        ps1.setDescription("Preschimbare permis de conducere");
        services.add(publicServiceRepository.save(ps1));
        
        PublicService ps2 = new PublicService();
        ps2.setName("Inmatriculare vehicul");
        ps2.setInstitutionType(Institution.InstitutionType.DRPCIV);
        ps2.setFee(150D);
        ps2.setRequiredDocuments("Talon");
        ps2.setEstimatedDuration(15D);
        ps2.setDescription("Inmatriculare/Transcriere vehicul");
        services.add(publicServiceRepository.save(ps2));
        
        return services;
    }
}
