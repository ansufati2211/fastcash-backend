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

    @Transactional
    public Map<String, Object> registrarVenta(RegistroVentaRequest request) {
        
        // 1. Validar Caja
        SesionCaja sesion = cajaService.obtenerSesionActual(request.getUsuarioID());
        if (sesion == null) throw new RuntimeException("CAJA CERRADA: Debe abrir caja antes de vender.");

        // 2. Validar Operación Yape/Tarjeta
        if (request.getPagos() != null) {
            for (PagoVentaDTO pago : request.getPagos()) {
                if (!"EFECTIVO".equals(pago.getFormaPago())) {
                    if (pago.getNumOperacion() == null || pago.getNumOperacion().trim().isEmpty()) {
                        throw new RuntimeException("ERROR: Ingrese el N° de Operación para " + pago.getFormaPago());
                    }
                }
            }
        }

        try {
            // 3. Ejecutar SP con los DATOS OPCIONALES
            String sql = "EXEC sp_RegistrarVentaTransaccional " +
                         "@UsuarioID = ?, @TipoComprobanteID = ?, @ClienteDoc = ?, @ClienteNombre = ?, " +
                         "@JsonDetalles = ?, @JsonPagos = ?, " +
                         "@FechaPersonalizada = ?, @NumeroComprobanteManual = ?"; // <--- 8 Parámetros

            Map<String, Object> resultado = jdbcTemplate.queryForMap(sql,
                    request.getUsuarioID(),
                    request.getTipoComprobanteID(),
                    request.getClienteDoc(),
                    request.getClienteNombre(),
                    objectMapper.writeValueAsString(request.getDetalles()),
                    objectMapper.writeValueAsString(request.getPagos()),
                    request.getFechaEmision(),           // Pasa NULL si no seleccionó fecha
                    request.getNumeroComprobanteManual() // Pasa NULL si no escribió ticket
            );

            if ("ERROR".equals(resultado.get("Status"))) throw new RuntimeException((String) resultado.get("Mensaje"));
            return resultado;

        } catch (Exception e) {
            String msg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            throw new RuntimeException("Error al registrar: " + msg);
        }
    }

    // Método necesario para que el Controller no falle
    public List<Map<String, Object>> listarHistorialDia(Integer usuarioID) {
        return jdbcTemplate.queryForList("SELECT VentaID, CONCAT(SerieComprobante, '-', NumeroComprobante) as Comprobante, ImporteTotal, FORMAT(FechaEmision, 'HH:mm') as Hora, Estado FROM Ventas WHERE UsuarioID = ? AND CAST(FechaEmision AS DATE) = CAST(GETDATE() AS DATE) ORDER BY VentaID DESC", usuarioID);
    }
    
    // Método de anulación (Mantenido)
    @Transactional
    public Map<String, Object> anularVenta(AnulacionRequest request) {
        SesionCaja sesion = cajaService.obtenerSesionActual(request.getUsuarioID());
        if (sesion == null) throw new RuntimeException("Caja cerrada.");
        return jdbcTemplate.queryForMap("EXEC sp_Operacion_AnularVenta ?, ?, ?", request.getVentaID(), request.getUsuarioID(), request.getMotivo());
    }
}