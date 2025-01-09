package com.example.Formation.services;

import com.example.Formation.entities.Formation;
import com.example.Formation.repository.FormationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FormationService {
    @Autowired
    private FormationRepository formationRepository;

    public List<Formation> getAllFormations() {
        return formationRepository.findAll();
    }

    public Formation getFormationById(Long id) {
        return formationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formation not found"));
    }

    public Formation saveFormation(Formation formation) {
        return formationRepository.save(formation);
    }

    public void deleteFormation(Long id) {
        formationRepository.deleteById(id);
    }
}
