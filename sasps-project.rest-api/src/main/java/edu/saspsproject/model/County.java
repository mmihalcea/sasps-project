package edu.saspsproject.model;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class County {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column( nullable = false)
    private String name;
}
