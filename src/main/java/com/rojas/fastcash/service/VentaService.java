package com.rojas.fastcash.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rojas.fastcash.dto.RegistroVentaRequest;
import com.rojas.fastcash.dto.AnulacionRequest;
import com.rojas.fastcash.dto.PagoVentaDTO; // <--- Importante para validar pagos
import com.rojas.fastcash.entity.SesionCaja;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class VentaService {

    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private CajaService cajaService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public Map<String, Object> registrarVenta(RegistroVentaRequest request) {
        
        // 1. REGLA DE NEGOCIO: Caja Abierta
        SesionCaja sesion = cajaService.obtenerSesionActual(request.getUsuarioID());
        if (sesion == null) {
            throw new RuntimeException("CAJA CERRADA: El usuario debe abrir caja antes de vender.");
        }

        // 2. REGLA DE NEGOCIO: Auditoría de Pagos (Validar Yape/Tarjeta)
        if (request.getPagos() != null) {
            for (PagoVentaDTO pago : request.getPagos()) {
                // Si NO es efectivo, exigimos el código de operación
                if (!"EFECTIVO".equals(pago.getFormaPago())) {
                    if (pago.getNumOperacion() == null || pago.getNumOperacion().trim().isEmpty()) {
                        throw new RuntimeException("ERROR: Debe ingresar el N° de Operación para pagos con " + pago.getFormaPago());
                    }
                }
            }
        }

        try {
            // 3. Serializar a JSON
            String jsonDetalles = objectMapper.writeValueAsString(request.getDetalles());
            String jsonPagos = objectMapper.writeValueAsString(request.getPagos());

            // 4. Llamar al SP Poderoso (Actualizado con Fecha Personalizada)
            // Nota: El SP ahora recibe 7 parámetros
            String sql = "EXEC sp_RegistrarVentaTransaccional " +
                         "@UsuarioID = ?, " +
                         "@TipoComprobanteID = ?, " +
                         "@ClienteDoc = ?, " +
                         "@ClienteNombre = ?, " +
                         "@JsonDetalles = ?, " +
                         "@JsonPagos = ?, " +
                         "@FechaPersonalizada = ?"; // <--- Nuevo parámetro

            // 5. Ejecutar
            Map<String, Object> resultado = jdbcTemplate.queryForMap(sql,
                    request.getUsuarioID(),
                    request.getTipoComprobanteID(),
                    request.getClienteDoc(),
                    request.getClienteNombre(),
                    jsonDetalles,
                    jsonPagos,
                    request.getFechaEmision() // <--- Pasamos la fecha (puede ser null)
            );

            // 6. Validar respuesta del SP
            if ("ERROR".equals(resultado.get("Status"))) {
                throw new RuntimeException((String) resultado.get("Mensaje"));
            }

            return resultado;

        } catch (Exception e) {
            // Captura errores SQL o de lógica y limpia el mensaje
            String errorMsg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            throw new RuntimeException("Error al procesar venta: " + errorMsg);
        }
    }

    // ==========================================
    // MÓDULO 6: ANULACIONES
    // ==========================================
    @Transactional
    public Map<String, Object> anularVenta(AnulacionRequest request) {
        // Validar caja abierta para auditoría (quién anuló y en qué turno)
        SesionCaja sesion = cajaService.obtenerSesionActual(request.getUsuarioID());
        if (sesion == null) {
            throw new RuntimeException("No puede realizar anulaciones sin una caja abierta.");
        }

        try {
            String sql = "EXEC sp_Operacion_AnularVenta @VentaID = ?, @UsuarioID = ?, @Motivo = ?";
            
            return jdbcTemplate.queryForMap(sql, 
                request.getVentaID(), 
                request.getUsuarioID(), 
                request.getMotivo()
            );

        } catch (Exception e) {
            String errorMsg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            throw new RuntimeException("Error al anular: " + errorMsg);
        }
    }
}