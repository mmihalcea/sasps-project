package edu.saspsproject.controller;

import edu.saspsproject.dto.response.InstitutionDetailResponse;
import edu.saspsproject.model.Institution;
import edu.saspsproject.repository.InstitutionRepository;
import edu.saspsproject.service.InstitutionDataLoader;
import edu.saspsproject.service.InstitutionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/institution")
@RestController
public class InstitutionController {
    private final InstitutionRepository repo;
    private final InstitutionService institutionService;
    private final InstitutionDataLoader dataLoader;

    public InstitutionController(InstitutionRepository repo, InstitutionService institutionService, InstitutionDataLoader dataLoader) {
        this.repo = repo;
        this.institutionService = institutionService;
        this.dataLoader = dataLoader;
    }

    @GetMapping
    public List<Institution> all() {
        return repo.findAll();
    }

    @PostMapping
    public Institution add(@RequestBody Institution institution) {
        return repo.save(institution);
    }

    @PostMapping("/load-data")
    public ResponseEntity<String> loadInstitutions() {
        try {
            dataLoader.loadSimpleInstitutions();
            return ResponseEntity.ok("Institutions loaded successfully. Total: " + repo.count());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{institutionType}")
    public ResponseEntity<InstitutionDetailResponse> getInstitutionDetails(@PathVariable String institutionType) {
        try {
            var institutionDetails = institutionService.getInstitutionDetailsByType(institutionType);
            return ResponseEntity.ok(institutionDetails);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
