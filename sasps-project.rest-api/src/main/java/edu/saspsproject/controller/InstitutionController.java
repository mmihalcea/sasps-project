package edu.saspsproject.controller;

import edu.saspsproject.model.Institution;
import edu.saspsproject.repository.InstitutionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/institution")
@RestController
public class InstitutionController {
    private final InstitutionRepository repo;

    public InstitutionController(InstitutionRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Institution> all() {
        return repo.findAll();
    }

    @PostMapping
    public Institution add(@RequestBody Institution institution) {
        return repo.save(institution);
    }
}
