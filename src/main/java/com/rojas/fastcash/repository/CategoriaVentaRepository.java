package com.rojas.fastcash.repository;

import com.rojas.fastcash.entity.CategoriaVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoriaVentaRepository extends JpaRepository<CategoriaVenta, Integer> {
    // SELECT * FROM CategoriasVenta WHERE Activo = 1
    List<CategoriaVenta> findByActivoTrue();
}