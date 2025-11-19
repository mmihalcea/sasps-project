package edu.saspsproject.controller;

import edu.saspsproject.model.Institution;
import edu.saspsproject.repository.InstitutionRepository;
import edu.saspsproject.service.InstitutionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/institution")
@RestController
public class InstitutionController {
    private final InstitutionRepository repo;
    private final InstitutionService institutionService;

    public InstitutionController(InstitutionRepository repo, InstitutionService institutionService) {
        this.repo = repo;
        this.institutionService = institutionService;
    }

    @GetMapping
    public List<Institution> all() {
        return repo.findAll();
    }

    @PostMapping
    public Institution add(@RequestBody(required = false) Institution institution) {
        if (institution != null) {
            return repo.save(institution);
        } else {
            institutionService.insertAllInstitutions();
        }
        return null;
    }
}
