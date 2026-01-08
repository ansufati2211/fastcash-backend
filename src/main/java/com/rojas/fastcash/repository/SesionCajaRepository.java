package com.rojas.fastcash.repository;

import com.rojas.fastcash.entity.SesionCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface SesionCajaRepository extends JpaRepository<SesionCaja, Integer> {
    
    // Query para saber si un usuario ya tiene la caja abierta
    @Query("SELECT s FROM SesionCaja s WHERE s.usuario.usuarioID = :uid AND s.estado = 'ABIERTO'")
    Optional<SesionCaja> buscarSesionAbierta(@Param("uid") Integer usuarioID);
}