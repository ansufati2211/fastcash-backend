package com.rojas.fastcash.repository;

import com.rojas.fastcash.entity.Turno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TurnoRepository extends JpaRepository<Turno, Integer> {
    // Queda limpio. Solo métodos básicos por si quieres listar horarios.
}