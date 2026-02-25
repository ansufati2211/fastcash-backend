package com.rojas.fastcash.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rojas.fastcash.dto.AnulacionRequest;
import com.rojas.fastcash.dto.PagoVentaDTO;
import com.rojas.fastcash.dto.RegistroVentaRequest;
import com.rojas.fastcash.entity.SesionCaja;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j // Agregamos Slf4j para manejo profesional de Logs
@Service
public class VentaService {

    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private CajaService cajaService;

    // 1. MEJORA: Inyección del ObjectMapper de Spring (Respeta configuraciones globales)
    @Autowired private ObjectMapper objectMapper;

    @Transactional
    public Map<String, Object> registrarVenta(RegistroVentaRequest request) {
        SesionCaja sesion = cajaService.obtenerSesionActual(request.getUsuarioID());
        if (sesion == null) {
            throw new RuntimeException("CAJA CERRADA: Debe abrir caja antes de vender.");
        }

        if (request.getPagos() != null) {
            for (PagoVentaDTO pago : request.getPagos()) {
                if (!"EFECTIVO".equals(pago.getFormaPago())) {
                    if (pago.getNumOperacion() == null || pago.getNumOperacion().trim().isEmpty()) {
                        throw new RuntimeException("ERROR: Ingrese N° Operación para " + pago.getFormaPago());
                    }
                }
            }
        }

        try {
            // 2. MEJORA CRÍTICA: Uso de ?::jsonb en lugar de ?::json
            String sql = "SELECT * FROM sp_ventas_registrar(?, ?, ?, ?, ?::jsonb, ?::jsonb, ?)";

            return jdbcTemplate.queryForMap(sql,
                    request.getUsuarioID(),
                    request.getTipoComprobanteID(),
                    request.getClienteDoc(),
                    request.getClienteNombre(),
                    objectMapper.writeValueAsString(request.getDetalles()),
                    objectMapper.writeValueAsString(request.getPagos()),
                    request.getComprobanteExterno()
            );

        } catch (Exception e) {
            // 3. MEJORA: Logs en consola sin bloquear el hilo principal
            log.error("❌ Error BD al registrar venta: {}", e.getMessage(), e);

            // 4. MEJORA: Manejo de errores seguro (Evita StringIndexOutOfBoundsException)
            String msg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();

            if (msg != null && msg.contains("ERROR:")) {
                try {
                    // Extraemos solo el mensaje de PostgreSQL de forma segura
                    msg = msg.split("ERROR:")[1].split("\n")[0].trim();
                } catch (Exception parseException) {
                    msg = "Error interno al procesar la respuesta de la base de datos.";
                }
            }
            throw new RuntimeException("Error: " + msg);
        }
    }

    public List<Map<String, Object>> listarHistorialDia(Integer usuarioID, Integer filtroUsuarioID) {
        String sql = "SELECT * FROM sp_historialventas_filtrado(?, ?)";
        return jdbcTemplate.queryForList(sql, usuarioID, filtroUsuarioID);
    }

    @Transactional
    public Map<String, Object> anularVenta(AnulacionRequest request) {
        try {
            String sql = "SELECT * FROM sp_operacion_anularventa(?, ?, ?)";
            return jdbcTemplate.queryForMap(sql, request.getVentaID(), request.getUsuarioID(), request.getMotivo());
        } catch (Exception e) {
            log.error("❌ Error BD al anular venta: {}", e.getMessage(), e);

            String msg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            if (msg != null && msg.contains("ERROR:")) {
                try {
                    msg = msg.split("ERROR:")[1].split("\n")[0].trim();
                } catch (Exception parseException) {
                    msg = "Error interno al procesar la anulación.";
                }
            }
            throw new RuntimeException("Error al anular: " + msg);
        }
    }
}
