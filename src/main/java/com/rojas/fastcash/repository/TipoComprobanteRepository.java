package com.rojas.fastcash.repository;

import com.rojas.fastcash.entity.TipoComprobante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoComprobanteRepository extends JpaRepository<TipoComprobante, Integer> {
    // Aquí usamos findAll() por defecto
}