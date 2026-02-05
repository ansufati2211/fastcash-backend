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

import java.sql.PreparedStatement;
import java.sql.Types; // <--- 1. ¡ESTE IMPORT ES LA CLAVE QUE FALTABA!
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
            throw new RuntimeException("⚠️ Ya tienes una caja ABIERTA.");
        }
        
        // Aquí te funcionaba porque usabas PreparedStatement manual
        String sql = "SELECT sp_caja_abrir(?, ?)";
        BigDecimal saldo = request.getSaldoInicial() != null ? request.getSaldoInicial() : BigDecimal.ZERO;
        
        try {
            jdbcTemplate.execute(sql, (PreparedStatement ps) -> {
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

        String sql = "SELECT * FROM sp_caja_cerrar(?, ?)";
        BigDecimal saldoFinal = request.getSaldoFinalReal() != null ? request.getSaldoFinalReal() : BigDecimal.ZERO;

        try {
            // 2. CORRECCIÓN: Definimos explícitamente los TIPOS DE DATOS.
            // Si no hacemos esto, Postgres recibe "tipos desconocidos" y lanza "Bad SQL Grammar".
            Object[] args = new Object[] { request.getUsuarioID(), saldoFinal };
            int[] argTypes = new int[] { Types.INTEGER, Types.NUMERIC }; // <--- ¡ESTO LO ARREGLA!

            return jdbcTemplate.queryForMap(sql, args, argTypes);

        } catch (Exception e) {
            System.err.println("❌ Error Cierre Caja: " + e.getMessage());
            throw new RuntimeException("Error al cerrar caja: " + e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "America/Lima") 
    public void cierreProgramadoMedianoche() {
        ejecutarLimpiezaCajas("Medianoche");
    }

    @EventListener(ApplicationReadyEvent.class)
    public void cierreAlIniciarSistema() {
        ejecutarLimpiezaCajas("InicioSistema");
    }

    private void ejecutarLimpiezaCajas(String origen) {
        try {
            jdbcTemplate.execute("SELECT * FROM sp_caja_cierreautomatico()");
            System.out.println("✅ [AUTO-CIERRE] Limpieza completada (Origen: " + origen + ").");
        } catch (Exception e) {
            System.err.println("❌ [AUTO-CIERRE] Error: " + e.getMessage());
        }
    }
}