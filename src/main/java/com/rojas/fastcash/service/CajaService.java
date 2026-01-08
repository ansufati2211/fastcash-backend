package com.rojas.fastcash.service;

import com.rojas.fastcash.dto.AperturaCajaRequest;
import com.rojas.fastcash.entity.SesionCaja;
import com.rojas.fastcash.repository.SesionCajaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CajaService {

    @Autowired private SesionCajaRepository sesionRepo;
    @Autowired private JdbcTemplate jdbcTemplate;

    public SesionCaja obtenerSesionActual(Integer usuarioID) {
        return sesionRepo.buscarSesionAbierta(usuarioID).orElse(null);
    }

    @Transactional
    public void abrirCaja(AperturaCajaRequest request) {
        // Ejecutamos el SP sp_Caja_Abrir
        String sql = "EXEC sp_Caja_Abrir @UsuarioID = ?, @SaldoInicial = ?";
        
        try {
            jdbcTemplate.update(sql, request.getUsuarioID(), request.getSaldoInicial());
        } catch (Exception e) {
            // Manejo de errores SQL (ej: "No tiene turno asignado")
            throw new RuntimeException(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
        }
    }
}