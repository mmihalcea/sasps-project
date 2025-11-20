package edu.saspsproject.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.saspsproject.dto.response.TownHallResponse;
import edu.saspsproject.model.County;
import edu.saspsproject.model.Institution;
import edu.saspsproject.model.PublicService;
import edu.saspsproject.repository.CountyRepository;

import edu.saspsproject.repository.InstitutionRepository;
import edu.saspsproject.repository.PublicServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstitutionService {


    private final CountyRepository countyRepository;
    private final ObjectMapper objectMapper;
    private final InstitutionRepository institutionRepository;
    private final PublicServiceRepository publicServiceRepository;



    public void insertAllInstitutions(){
        try {
            URI uri = ClassLoader.getSystemResource("town-halls").toURI();
            String path = Paths.get(uri).toString();
            //common institution services
            List<Institution> institutions = new ArrayList<>();
            Institution drpciv = new Institution();
            drpciv.setName("DRPCIV");
            drpciv.setAddress("DRPCIV Address");
            drpciv.setType(Institution.InstitutionType.DRPCIV);
            drpciv.setMaxAppointmentsPerDay(40);
            drpciv.setClosingTime(LocalTime.of(17, 0));
            drpciv.setAverageServiceTime(10D);
            drpciv.setOpeningTime(LocalTime.of(8, 0));

            List<PublicService> drpcivServices = new ArrayList<>();
            PublicService publicService1 = new PublicService();
            publicService1.setName("Preschimbare permis de conducere");
            publicService1.setInstitutionType(Institution.InstitutionType.DRPCIV);
            publicService1.setFee(100D);
            publicService1.setRequiredDocuments("Permis vechi");
            publicService1.setEstimatedDuration(10D);
            publicService1.setDescription("Preschimbare permis de conducere (expirat, pierdut, schimbare nume)");
            drpcivServices.add(publicServiceRepository.save(publicService1));

            PublicService publicService2 = new PublicService();
            publicService2.setName("Înmatriculare / Transcriere vehicul");
            publicService2.setInstitutionType(Institution.InstitutionType.DRPCIV);
            publicService2.setFee(150D);
            publicService2.setRequiredDocuments("Talon vechi");
            publicService2.setEstimatedDuration(10D);
            publicService2.setDescription("Înmatriculare / Transcriere vehicul");
            drpcivServices.add(publicServiceRepository.save(publicService2));

            PublicService publicService3 = new PublicService();
            publicService3.setName("Radiere vehicul");
            publicService3.setInstitutionType(Institution.InstitutionType.DRPCIV);
            publicService3.setFee(90D);
            publicService3.setRequiredDocuments("Buletin");
            publicService3.setEstimatedDuration(5D);
            publicService3.setDescription("Radiere vehicul");
            drpcivServices.add(publicServiceRepository.save(publicService3));

            PublicService publicService4 = new PublicService();
            publicService4.setName("Programare examen auto");
            publicService4.setInstitutionType(Institution.InstitutionType.DRPCIV);
            publicService4.setFee(120D);
            publicService4.setRequiredDocuments("Buletin");
            publicService4.setEstimatedDuration(5D);
            publicService4.setDescription("Programare examen auto");
            drpcivServices.add(publicServiceRepository.save(publicService4));

            drpciv.setAvailableServices(drpcivServices);
            institutions.add(drpciv);

            Institution spclep = new Institution();
            spclep.setName("SPCLEP");
            spclep.setAddress("SPCLEP Address");
            spclep.setType(Institution.InstitutionType.SPCLEP);
            spclep.setMaxAppointmentsPerDay(10);
            spclep.setClosingTime(LocalTime.of(15, 0));
            spclep.setAverageServiceTime(10D);
            spclep.setOpeningTime(LocalTime.of(10, 0));

            List<PublicService> spclepServices = new ArrayList<>();
            PublicService publicService5 = new PublicService();
            publicService5.setName("Eliberare carte de identitate");
            publicService5.setInstitutionType(Institution.InstitutionType.SPCLEP);
            publicService5.setFee(100D);
            publicService5.setRequiredDocuments("Certificat nastere");
            publicService5.setEstimatedDuration(30D);
            publicService5.setDescription("Eliberare carte de identitate (prima eliberare, expirare, pierdere, schimbare domiciliu)");
            spclepServices.add(publicServiceRepository.save(publicService5));

            PublicService publicService6 = new PublicService();
            publicService6.setName("Stabilire reședință");
            publicService6.setInstitutionType(Institution.InstitutionType.SPCLEP);
            publicService6.setFee(100D);
            publicService6.setRequiredDocuments("Buletin");
            publicService6.setEstimatedDuration(30D);
            publicService6.setDescription("Stabilire reședință (viză de flotant)");
            spclepServices.add(publicServiceRepository.save(publicService6));

            PublicService publicService7 = new PublicService();
            publicService7.setName("Eliberare carte de identitate provizorie");
            publicService7.setInstitutionType(Institution.InstitutionType.SPCLEP);
            publicService7.setFee(100D);
            publicService7.setRequiredDocuments("Certificat nastere");
            publicService7.setEstimatedDuration(30D);
            publicService7.setDescription("Eliberare carte de identitate provizorie");
            spclepServices.add(publicServiceRepository.save(publicService7));

            spclep.setAvailableServices(spclepServices);
            institutions.add(spclep);

            Institution anaf = new Institution();
            anaf.setName("ANAF");
            anaf.setAddress("ANAF Address");
            anaf.setType(Institution.InstitutionType.ANAF);
            anaf.setMaxAppointmentsPerDay(15);
            anaf.setClosingTime(LocalTime.of(15, 0));
            anaf.setAverageServiceTime(10D);
            anaf.setOpeningTime(LocalTime.of(10, 0));

            List<PublicService> anafServices = new ArrayList<>();
            PublicService publicService8 = new PublicService();
            publicService8.setName("Obținere / Eliberare cazier fiscal");
            publicService8.setInstitutionType(Institution.InstitutionType.ANAF);
            publicService8.setFee(60D);
            publicService8.setRequiredDocuments("Buletin");
            publicService8.setEstimatedDuration(15D);
            publicService8.setDescription("Obținere / Eliberare cazier fiscal");
            anafServices.add(publicServiceRepository.save(publicService8));

            PublicService publicService9 = new PublicService();
            publicService9.setName("Obținere certificat de atestare fiscală");
            publicService9.setInstitutionType(Institution.InstitutionType.ANAF);
            publicService9.setFee(60D);
            publicService9.setRequiredDocuments("Buletin");
            publicService9.setEstimatedDuration(20D);
            publicService9.setDescription("Obținere certificat de atestare fiscală (pentru persoane juridice)");
            anafServices.add(publicServiceRepository.save(publicService9));

            PublicService publicService10 = new PublicService();
            publicService10.setName("Înregistrare în scopuri de TVA / Radiere");
            publicService10.setInstitutionType(Institution.InstitutionType.ANAF);
            publicService10.setFee(50D);
            publicService10.setRequiredDocuments("Buletin");
            publicService10.setEstimatedDuration(20D);
            publicService10.setDescription("Înregistrare în scopuri de TVA / Radiere");
            anafServices.add(publicServiceRepository.save(publicService10));

            anaf.setAvailableServices(anafServices);
            institutions.add(anaf);


            Institution anpc = new Institution();
            anpc.setName("ANPC");
            anpc.setAddress("ANPC Address");
            anpc.setType(Institution.InstitutionType.ANPC);
            anpc.setMaxAppointmentsPerDay(10);
            anpc.setClosingTime(LocalTime.of(15, 0));
            anpc.setAverageServiceTime(10D);
            anpc.setOpeningTime(LocalTime.of(10, 0));

            List<PublicService> anpcServices = new ArrayList<>();
            PublicService publicService11 = new PublicService();
            publicService11.setName("Depunere reclamație / sesizare");
            publicService11.setInstitutionType(Institution.InstitutionType.ANPC);
            publicService11.setFee(0D);
            publicService11.setRequiredDocuments("Buletin");
            publicService11.setEstimatedDuration(15D);
            publicService11.setDescription("Depunere reclamație / sesizare");
            anpcServices.add(publicServiceRepository.save(publicService11));

            PublicService publicService12 = new PublicService();
            publicService12.setName("Program audiență");
            publicService12.setInstitutionType(Institution.InstitutionType.ANPC);
            publicService12.setFee(0D);
            publicService12.setRequiredDocuments("Buletin");
            publicService12.setEstimatedDuration(20D);
            publicService12.setDescription("Program audiență");
            anpcServices.add(publicServiceRepository.save(publicService12));

            anpc.setAvailableServices(anpcServices);
            institutions.add(anpc);


            List<PublicService> townHallServices = new ArrayList<>();

            PublicService publicService13 = new PublicService();
            publicService13.setName("Programare oficiere căsătorie");
            publicService13.setInstitutionType(Institution.InstitutionType.PRIMARIA);
            publicService13.setFee(100D);
            publicService13.setRequiredDocuments("Buletine");
            publicService13.setEstimatedDuration(60D);
            publicService13.setDescription("Programare oficiere căsătorie");
            townHallServices.add(publicServiceRepository.save(publicService13));

            PublicService publicService14 = new PublicService();
            publicService14.setName("Depunere documentație pentru Certificat de Urbanism (CU)");
            publicService14.setInstitutionType(Institution.InstitutionType.PRIMARIA);
            publicService14.setFee(90D);
            publicService14.setRequiredDocuments("Buletin, Drept de proprietate");
            publicService14.setEstimatedDuration(30D);
            publicService14.setDescription("Depunere documentație pentru Certificat de Urbanism (CU)");
            townHallServices.add(publicServiceRepository.save(publicService14));

            PublicService publicService15 = new PublicService();
            publicService15.setName("Eliberare adeverințe (număr poștal, nomenclator stradal)");
            publicService15.setInstitutionType(Institution.InstitutionType.PRIMARIA);
            publicService15.setFee(60D);
            publicService15.setRequiredDocuments("Buletin, Drept de proprietate");
            publicService15.setEstimatedDuration(30D);
            publicService15.setDescription("Eliberare adeverințe (număr poștal, nomenclator stradal)");
            townHallServices.add(publicServiceRepository.save(publicService15));

            PublicService publicService16 = new PublicService();
            publicService16.setName("Eliberare duplicate certificate");
            publicService16.setInstitutionType(Institution.InstitutionType.PRIMARIA);
            publicService16.setFee(50D);
            publicService16.setRequiredDocuments("Buletin, Certificat original");
            publicService16.setEstimatedDuration(20D);
            publicService16.setDescription("Eliberare duplicate certificate (naștere, căsătorie, deces)");
            townHallServices.add(publicServiceRepository.save(publicService16));

            PublicService publicService17 = new PublicService();
            publicService17.setName("Depunere declarații fiscale (clădiri, auto, etc.)");
            publicService17.setInstitutionType(Institution.InstitutionType.PRIMARIA);
            publicService17.setFee(50D);
            publicService17.setRequiredDocuments("Buletin, Certificat original");
            publicService17.setEstimatedDuration(20D);
            publicService17.setDescription("Depunere declarații fiscale (clădiri, auto, etc.)");
            townHallServices.add(publicServiceRepository.save(publicService17));

            institutionRepository.saveAll(institutions);

            Files.list((Paths.get(path))).sorted().filter((Files::isRegularFile))
                    .forEach(filePath ->{
                        File file = filePath.toFile();
                        String countyName = file.getName().replace(".json", "");
                        County county = countyRepository.findByName(countyName).orElse(countyRepository.save(new County(countyName)));

                            try {
                                List<TownHallResponse> townHalls = objectMapper.readValue(file, new TypeReference<List<TownHallResponse>>() {});
                                List<Institution> townHallInstitutions = new ArrayList<>(townHalls.stream().map(townHallDto -> {
                                    Institution institution = new Institution();
                                    institution.setName(townHallDto.getName());
                                    institution.setCounty(county);
                                    institution.setAddress(townHallDto.getAddress().getStreet() + ", " + townHallDto.getAddress().getCity().getName() + ", " + townHallDto.getAddress().getPostalCode());
                                    if (townHallDto.getContact() != null && townHallDto.getContact().getPhoneNumbers() != null) {
                                        institution.setPhone(townHallDto.getContact().getPhoneNumbers().get(0));
                                    }
                                    institution.setType(Institution.InstitutionType.PRIMARIA);
                                    institution.setMaxAppointmentsPerDay(20);
                                    institution.setClosingTime(LocalTime.of(16, 0));
                                    institution.setAverageServiceTime(15D);
                                    institution.setOpeningTime(LocalTime.of(9, 0));
                                    institution.setAvailableServices(townHallServices);
                                    return institution;
                                }).toList());

                                institutionRepository.saveAll(townHallInstitutions);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                    });

        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

}
