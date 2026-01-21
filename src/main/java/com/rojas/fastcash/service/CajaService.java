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
import java.util.Optional; // Importación necesaria

@Service
public class CajaService {

    @Autowired private SesionCajaRepository sesionRepo;
    @Autowired private JdbcTemplate jdbcTemplate;

    public SesionCaja obtenerSesionActual(Integer usuarioID) {
        return sesionRepo.buscarSesionAbierta(usuarioID).orElse(null);
    }

    @Transactional
    public void abrirCaja(AperturaCajaRequest request) {
        
        // =================================================================================
        // 1. VALIDACIÓN DE SEGURIDAD (LO QUE FALTABA)
        // =================================================================================
        // Antes de llamar a la BD, verificamos en Java si ya tiene una sesión activa.
        Optional<SesionCaja> sesionActual = sesionRepo.buscarSesionAbierta(request.getUsuarioID());
        
        if (sesionActual.isPresent()) {
            // Si entra aquí, es porque TIENE una caja abierta. Bloqueamos.
            throw new RuntimeException("⚠️ Ya tienes una caja ABIERTA. Debes cerrar la actual antes de abrir una nueva.");
        }
        
        // Si pasa la validación (es decir, no tiene caja abierta o la anterior ya está CERRADA),
        // procedemos a crear la nueva sesión.
        
        String sql = "EXEC sp_Caja_Abrir @UsuarioID = ?, @SaldoInicial = ?";
        
        // LOGICA DE NEGOCIO: Si es nulo, mandamos CERO.
        BigDecimal saldo = request.getSaldoInicial() != null ? request.getSaldoInicial() : BigDecimal.ZERO;
        
        try {
            jdbcTemplate.update(sql, request.getUsuarioID(), saldo);
        } catch (Exception e) {
            // Mejoramos el mensaje de error para que sea legible en el Frontend
            String msg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            throw new RuntimeException("Error al abrir caja: " + msg);
        }
    }

    @Transactional
    public Map<String, Object> cerrarCaja(CierreCajaRequest request) {
        // Validamos también al cerrar, por seguridad
        if (sesionRepo.buscarSesionAbierta(request.getUsuarioID()).isEmpty()) {
            throw new RuntimeException("⚠️ No tienes una caja abierta para cerrar.");
        }

        String sql = "EXEC sp_Caja_Cerrar @UsuarioID = ?, @SaldoFinalReal = ?";
        try {
            return jdbcTemplate.queryForMap(sql, request.getUsuarioID(), request.getSaldoFinalReal());
        } catch (Exception e) {
            throw new RuntimeException("Error al cerrar caja: " + e.getMessage());
        }
    }
}