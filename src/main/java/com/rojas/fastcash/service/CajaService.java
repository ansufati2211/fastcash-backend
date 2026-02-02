package com.rojas.fastcash.service;

import com.rojas.fastcash.dto.AperturaCajaRequest;
import com.rojas.fastcash.dto.CierreCajaRequest;
import com.rojas.fastcash.entity.SesionCaja;
import com.rojas.fastcash.repository.SesionCajaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent; // <--- IMPORTAR
import org.springframework.context.event.EventListener;             // <--- IMPORTAR
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

    // ... (Tus métodos abrirCaja, cerrarCaja, obtenerSesionActual siguen igual) ...
    public SesionCaja obtenerSesionActual(Integer usuarioID) {
        return sesionRepo.buscarSesionAbierta(usuarioID).orElse(null);
    }

    @Transactional
    public void abrirCaja(AperturaCajaRequest request) {
        Optional<SesionCaja> sesionActual = sesionRepo.buscarSesionAbierta(request.getUsuarioID());
        if (sesionActual.isPresent()) {
            throw new RuntimeException("⚠️ Ya tienes una caja ABIERTA. Debes cerrar la actual antes de abrir una nueva.");
        }
        String sql = "EXEC sp_Caja_Abrir @UsuarioID = ?, @SaldoInicial = ?";
        BigDecimal saldo = request.getSaldoInicial() != null ? request.getSaldoInicial() : BigDecimal.ZERO;
        try {
            jdbcTemplate.update(sql, request.getUsuarioID(), saldo);
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
        String sql = "EXEC sp_Caja_Cerrar @UsuarioID = ?, @SaldoFinalReal = ?";
        try {
            return jdbcTemplate.queryForMap(sql, request.getUsuarioID(), request.getSaldoFinalReal());
        } catch (Exception e) {
            throw new RuntimeException("Error al cerrar caja: " + e.getMessage());
        }
    }

    // =========================================================================
    // LÓGICA DE CIERRE AUTOMÁTICO (DOBLE CHECK)
    // =========================================================================

    // 1. EJECUCIÓN PROGRAMADA: Medianoche (00:00 AM)
    @Scheduled(cron = "0 0 0 * * ?", zone = "America/Lima") 
    public void cierreProgramadoMedianoche() {
        ejecutarLimpiezaCajas("Medianoche");
    }

    // 2. EJECUCIÓN AL INICIO: Por si el servidor estaba apagado en la noche
    @EventListener(ApplicationReadyEvent.class)
    public void cierreAlIniciarSistema() {
        // Esperamos unos segundos para asegurar que la BD esté lista
        System.out.println("🚀 Sistema Iniciado. Verificando cajas olvidadas de días anteriores...");
        ejecutarLimpiezaCajas("InicioSistema");
    }

    // Método privado que reutilizamos
    private void ejecutarLimpiezaCajas(String origen) {
        try {
            // Ejecutamos el SP (que ahora es seguro y solo cierra cajas viejas)
            jdbcTemplate.update("EXEC sp_Caja_CierreAutomatico");
            System.out.println("✅ [AUTO-CIERRE] Limpieza completada (Origen: " + origen + ").");
        } catch (Exception e) {
            System.err.println("❌ [AUTO-CIERRE] Error ejecutando limpieza: " + e.getMessage());
        }
    }
}