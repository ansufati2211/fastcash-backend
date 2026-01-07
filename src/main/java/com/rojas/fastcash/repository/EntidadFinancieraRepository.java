package com.rojas.fastcash.repository;

import com.rojas.fastcash.entity.EntidadFinanciera;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EntidadFinancieraRepository extends JpaRepository<EntidadFinanciera, Integer> {
    // SELECT * FROM EntidadesFinancieras WHERE Activo = 1
    List<EntidadFinanciera> findByActivoTrue();
}