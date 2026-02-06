package com.rojas.fastcash.repository;

import com.rojas.fastcash.entity.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Integer> {
    // Listo, ya puedes usar turnoRepo.findAll()
}