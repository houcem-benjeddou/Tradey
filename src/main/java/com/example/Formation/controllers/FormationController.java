package com.example.Formation.controllers;

import com.example.Formation.entities.Formation;
import com.example.Formation.services.FormationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formations")
@CrossOrigin("*")
public class FormationController {
    @Autowired
    private FormationService formationService;

    @GetMapping
    public List<Formation> getAllFormations() {
        return formationService.getAllFormations();
    }

    @GetMapping("/{id}")
    public Formation getFormationById(@PathVariable Long id) {
        return formationService.getFormationById(id);
    }

    @PostMapping
    public Formation createFormation(@RequestBody Formation formation) {
        return formationService.saveFormation(formation);
    }

    @DeleteMapping("/{id}")
    public void deleteFormation(@PathVariable Long id) {
        formationService.deleteFormation(id);
    }
}
