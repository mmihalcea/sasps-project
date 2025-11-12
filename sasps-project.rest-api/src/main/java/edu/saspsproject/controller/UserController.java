package edu.saspsproject.controller;

import edu.saspsproject.model.User;
import edu.saspsproject.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/user")
@RestController
public class UserController {
    private final UserRepository repo;

    public UserController(UserRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<User> all() {
        return repo.findAll();
    }

    @PostMapping
    public User add(@RequestBody User u) {
        return repo.save(u);
    }
}