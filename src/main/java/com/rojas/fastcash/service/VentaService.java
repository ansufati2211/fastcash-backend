package com.rojas.fastcash.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rojas.fastcash.dto.AnulacionRequest;
import com.rojas.fastcash.dto.PagoVentaDTO;
import com.rojas.fastcash.dto.RegistroVentaRequest;
import com.rojas.fastcash.entity.SesionCaja;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class VentaService {

    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private CajaService cajaService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ==========================================
    // 1. REGISTRAR VENTA (Con Fecha y Ticket Manual)
    // ==========================================
    @Transactional
    public Map<String, Object> registrarVenta(RegistroVentaRequest request) {
        
        // Validar Caja Abierta
        SesionCaja sesion = cajaService.obtenerSesionActual(request.getUsuarioID());
        if (sesion == null) {
            throw new RuntimeException("CAJA CERRADA: Debe abrir caja antes de vender.");
        }

        // Validar Pagos Digitales (Yape/Tarjeta requieren código)
        if (request.getPagos() != null) {
            for (PagoVentaDTO pago : request.getPagos()) {
                if (!"EFECTIVO".equals(pago.getFormaPago())) {
                    if (pago.getNumOperacion() == null || pago.getNumOperacion().trim().isEmpty()) {
                        throw new RuntimeException("ERROR: Debe ingresar el N° de Operación para " + pago.getFormaPago());
                    }
                }
            }
        }

        try {
            // Preparar JSONs para SQL
            String jsonDetalles = objectMapper.writeValueAsString(request.getDetalles());
            String jsonPagos = objectMapper.writeValueAsString(request.getPagos());

            // Llamar al SP (8 Parámetros)
            String sql = "EXEC sp_RegistrarVentaTransaccional " +
                         "@UsuarioID = ?, " +
                         "@TipoComprobanteID = ?, " +
                         "@ClienteDoc = ?, " +
                         "@ClienteNombre = ?, " +
                         "@JsonDetalles = ?, " +
                         "@JsonPagos = ?, " +
                         "@FechaPersonalizada = ?, " + 
                         "@NumeroComprobanteManual = ?";

            Map<String, Object> resultado = jdbcTemplate.queryForMap(sql,
                    request.getUsuarioID(),
                    request.getTipoComprobanteID(),
                    request.getClienteDoc(),
                    request.getClienteNombre(),
                    jsonDetalles,
                    jsonPagos,
                    request.getFechaEmision(),           // Fecha manual (o null)
                    request.getNumeroComprobanteManual() // Ticket manual (o null)
            );

            if ("ERROR".equals(resultado.get("Status"))) {
                throw new RuntimeException((String) resultado.get("Mensaje"));
            }

            return resultado;

        } catch (Exception e) {
            String errorMsg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            throw new RuntimeException("Error venta: " + errorMsg);
        }
    }

    // ==========================================
    // 2. ANULAR VENTA
    // ==========================================
    @Transactional
    public Map<String, Object> anularVenta(AnulacionRequest request) {
        SesionCaja sesion = cajaService.obtenerSesionActual(request.getUsuarioID());
        if (sesion == null) {
            throw new RuntimeException("No puede anular sin caja abierta.");
        }
        try {
            String sql = "EXEC sp_Operacion_AnularVenta @VentaID = ?, @UsuarioID = ?, @Motivo = ?";
            return jdbcTemplate.queryForMap(sql, request.getVentaID(), request.getUsuarioID(), request.getMotivo());
        } catch (Exception e) {
            String errorMsg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            throw new RuntimeException("Error anulación: " + errorMsg);
        }
    }

    // ==========================================
    // 3. LISTAR HISTORIAL DEL DÍA (MÉTODO FALTANTE)
    // ==========================================
    public List<Map<String, Object>> listarHistorialDia(Integer usuarioID) {
        // Consulta simple para llenar la tablita de ventas recientes del cajero
        String sql = "SELECT VentaID, " +
                     "CONCAT(SerieComprobante, '-', NumeroComprobante) AS Comprobante, " +
                     "ImporteTotal, " +
                     "FORMAT(FechaEmision, 'HH:mm') AS Hora, " +
                     "Estado " +
                     "FROM Ventas " +
                     "WHERE UsuarioID = ? " +
                     "AND CAST(FechaEmision AS DATE) = CAST(GETDATE() AS DATE) " + // Solo ventas de hoy
                     "ORDER BY VentaID DESC"; // Las más recientes primero
                     
        return jdbcTemplate.queryForList(sql, usuarioID);
    }
}