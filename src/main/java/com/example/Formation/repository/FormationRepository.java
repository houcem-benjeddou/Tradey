package com.example.Formation.repository;

import com.example.Formation.entities.Formation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormationRepository extends JpaRepository<Formation, Long> {
}