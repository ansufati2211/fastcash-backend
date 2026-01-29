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
            // 3. Ejecutar SP (ACTUALIZADO)
            // Nota: Cambié el nombre a 'sp_Ventas_Registrar' para coincidir con el script SQL
            // y agregué el parámetro @ComprobanteExterno al final.
            String sql = "EXEC sp_Ventas_Registrar @UsuarioID=?, @TipoComprobanteID=?, @ClienteDoc=?, @ClienteNombre=?, @JsonDetalles=?, @JsonPagos=?, @ComprobanteExterno=?";
            
            return jdbcTemplate.queryForMap(sql,
                    request.getUsuarioID(),
                    request.getTipoComprobanteID(),
                    request.getClienteDoc(),
                    request.getClienteNombre(),
                    objectMapper.writeValueAsString(request.getDetalles()),
                    objectMapper.writeValueAsString(request.getPagos()),
                    request.getComprobanteExterno() // <--- AQUÍ PASAMOS EL DATO NUEVO
            );

        } catch (Exception e) {
            String msg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            throw new RuntimeException("Error: " + msg);
        }
    }

    // =========================================================================
    // LISTAR HISTORIAL
    // =========================================================================
    public List<Map<String, Object>> listarHistorialDia(Integer usuarioID, Integer filtroUsuarioID) {
        // Asegúrate de que este SP (sp_HistorialVentas_Filtrado) 
        // tenga la lógica del CASE que te pasé para ver el Código Externo.
        String sql = "EXEC sp_HistorialVentas_Filtrado ?, ?";
        return jdbcTemplate.queryForList(sql, usuarioID, filtroUsuarioID);
    }

    @Transactional
    public Map<String, Object> anularVenta(AnulacionRequest request) {
        return jdbcTemplate.queryForMap("EXEC sp_Operacion_AnularVenta ?, ?, ?", request.getVentaID(), request.getUsuarioID(), request.getMotivo());
    }
}