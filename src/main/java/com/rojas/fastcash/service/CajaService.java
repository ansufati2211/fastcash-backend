package com.rojas.fastcash.service;

import com.rojas.fastcash.dto.AperturaCajaRequest;
import com.rojas.fastcash.dto.CierreCajaRequest;
import com.rojas.fastcash.entity.SesionCaja;
import com.rojas.fastcash.repository.SesionCajaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Map;

@Service
public class CajaService {

    @Autowired private SesionCajaRepository sesionRepo;
    @Autowired private JdbcTemplate jdbcTemplate;

    public SesionCaja obtenerSesionActual(Integer usuarioID) {
        return sesionRepo.buscarSesionAbierta(usuarioID).orElse(null);
    }

    @Transactional
    public void abrirCaja(AperturaCajaRequest request) {
        String sql = "EXEC sp_Caja_Abrir @UsuarioID = ?, @SaldoInicial = ?";
        
        // LOGICA DE NEGOCIO: Si es nulo, mandamos CERO.
        BigDecimal saldo = request.getSaldoInicial() != null ? request.getSaldoInicial() : BigDecimal.ZERO;
        
        try {
            jdbcTemplate.update(sql, request.getUsuarioID(), saldo);
        } catch (Exception e) {
            throw new RuntimeException(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
        }
    }

    @Transactional
    public Map<String, Object> cerrarCaja(CierreCajaRequest request) {
        String sql = "EXEC sp_Caja_Cerrar @UsuarioID = ?, @SaldoFinalReal = ?";
        try {
            return jdbcTemplate.queryForMap(sql, request.getUsuarioID(), request.getSaldoFinalReal());
        } catch (Exception e) {
            throw new RuntimeException("Error al cerrar caja: " + e.getMessage());
        }
    }
}