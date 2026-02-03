package com.rojas.fastcash.service;

import com.rojas.fastcash.dto.AperturaCajaRequest;
import com.rojas.fastcash.dto.CierreCajaRequest;
import com.rojas.fastcash.entity.SesionCaja;
import com.rojas.fastcash.repository.SesionCajaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Service
public class CajaService {

    @Autowired private SesionCajaRepository sesionRepo;
    @Autowired private JdbcTemplate jdbcTemplate;

    public SesionCaja obtenerSesionActual(Integer usuarioID) {
        return sesionRepo.buscarSesionAbierta(usuarioID).orElse(null);
    }

    @Transactional
    public void abrirCaja(AperturaCajaRequest request) {
        Optional<SesionCaja> sesionActual = sesionRepo.buscarSesionAbierta(request.getUsuarioID());
        if (sesionActual.isPresent()) {
            throw new RuntimeException("⚠️ Ya tienes una caja ABIERTA. Debes cerrar la actual antes de abrir una nueva.");
        }
        
        // POSTGRES: SELECT para llamar función
        String sql = "SELECT sp_caja_abrir(?, ?)";
        BigDecimal saldo = request.getSaldoInicial() != null ? request.getSaldoInicial() : BigDecimal.ZERO;
        
        try {
            jdbcTemplate.execute(sql, (org.springframework.jdbc.core.PreparedStatementCallback<Boolean>) ps -> {
                ps.setInt(1, request.getUsuarioID());
                ps.setBigDecimal(2, saldo);
                return ps.execute();
            });
        } catch (Exception e) {
            String msg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            throw new RuntimeException("Error al abrir caja: " + msg);
        }
    }

    @Transactional
    public Map<String, Object> cerrarCaja(CierreCajaRequest request) {
        if (sesionRepo.buscarSesionAbierta(request.getUsuarioID()).isEmpty()) {
            throw new RuntimeException("⚠️ No tienes una caja abierta para cerrar.");
        }
        // POSTGRES: SELECT * FROM funcion()
        String sql = "SELECT * FROM sp_caja_cerrar(?, ?)";
        try {
            return jdbcTemplate.queryForMap(sql, request.getUsuarioID(), request.getSaldoFinalReal());
        } catch (Exception e) {
            throw new RuntimeException("Error al cerrar caja: " + e.getMessage());
        }
    }

    // LÓGICA DE CIERRE AUTOMÁTICO
    @Scheduled(cron = "0 0 0 * * ?", zone = "America/Lima") 
    public void cierreProgramadoMedianoche() {
        ejecutarLimpiezaCajas("Medianoche");
    }

    @EventListener(ApplicationReadyEvent.class)
    public void cierreAlIniciarSistema() {
        System.out.println("🚀 Sistema Iniciado. Verificando cajas olvidadas...");
        ejecutarLimpiezaCajas("InicioSistema");
    }

    private void ejecutarLimpiezaCajas(String origen) {
        try {
            // POSTGRES
            jdbcTemplate.queryForList("SELECT * FROM sp_caja_cierreautomatico()");
            System.out.println("✅ [AUTO-CIERRE] Limpieza completada (Origen: " + origen + ").");
        } catch (Exception e) {
            System.err.println("❌ [AUTO-CIERRE] Error ejecutando limpieza: " + e.getMessage());
        }
    }
}