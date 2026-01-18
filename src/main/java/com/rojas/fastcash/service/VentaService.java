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

        // 2. Validar Yape/Tarjeta
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
            // 3. Ejecutar SP
            String sql = "EXEC sp_RegistrarVentaTransaccional @UsuarioID=?, @TipoComprobanteID=?, @ClienteDoc=?, @ClienteNombre=?, @JsonDetalles=?, @JsonPagos=?";
            
            return jdbcTemplate.queryForMap(sql,
                    request.getUsuarioID(),
                    request.getTipoComprobanteID(),
                    request.getClienteDoc(),
                    request.getClienteNombre(),
                    objectMapper.writeValueAsString(request.getDetalles()),
                    objectMapper.writeValueAsString(request.getPagos())
            );

        } catch (Exception e) {
            String msg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            throw new RuntimeException("Error: " + msg);
        }
    }

    // =========================================================================
    // LISTAR HISTORIAL (CORREGIDO: USANDO SP PARA TRAER CAJERO Y LÓGICA ADMIN)
    // =========================================================================
    public List<Map<String, Object>> listarHistorialDia(Integer usuarioID) {
        // CORRECCIÓN: Llamamos al SP que ya configuraste en SQL Server.
        // Este SP devuelve la columna 'Cajero' (con el username) y filtra si es Admin o Cajero.
        String sql = "EXEC sp_Ventas_HistorialDia ?";
        
        return jdbcTemplate.queryForList(sql, usuarioID);
    }

    @Transactional
    public Map<String, Object> anularVenta(AnulacionRequest request) {
        return jdbcTemplate.queryForMap("EXEC sp_Operacion_AnularVenta ?, ?, ?", request.getVentaID(), request.getUsuarioID(), request.getMotivo());
    }
}