package com.rojas.fastcash.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rojas.fastcash.dto.RegistroVentaRequest;
import com.rojas.fastcash.dto.AnulacionRequest; // Importamos el DTO nuevo
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
    
    // Instancia manual de Jackson para convertir objetos a JSON
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public Map<String, Object> registrarVenta(RegistroVentaRequest request) {
        
        // 1. REGLA DE NEGOCIO: No se puede vender si la caja no está abierta
        SesionCaja sesion = cajaService.obtenerSesionActual(request.getUsuarioID());
        if (sesion == null) {
            throw new RuntimeException("CAJA CERRADA: El usuario debe abrir caja antes de vender.");
        }

        try {
            // 2. Convertir las listas de Java a texto JSON para SQL Server
            String jsonDetalles = objectMapper.writeValueAsString(request.getDetalles());
            String jsonPagos = objectMapper.writeValueAsString(request.getPagos());

            // 3. Llamar al SP Poderoso (sp_RegistrarVentaTransaccional)
            String sql = "EXEC sp_RegistrarVentaTransaccional " +
                         "@UsuarioID = ?, " +
                         "@TipoComprobanteID = ?, " +
                         "@ClienteDoc = ?, " +
                         "@ClienteNombre = ?, " +
                         "@JsonDetalles = ?, " +
                         "@JsonPagos = ?";

            // 4. Ejecutar y capturar la respuesta
            Map<String, Object> resultado = jdbcTemplate.queryForMap(sql,
                    request.getUsuarioID(),
                    request.getTipoComprobanteID(),
                    request.getClienteDoc(),
                    request.getClienteNombre(),
                    jsonDetalles,
                    jsonPagos
            );

            // 5. Validar respuesta del SP
            if ("ERROR".equals(resultado.get("Status"))) {
                throw new RuntimeException((String) resultado.get("Mensaje"));
            }

            return resultado;

        } catch (Exception e) {
            throw new RuntimeException("Error al procesar la venta: " + e.getMessage());
        }
    }

    // ==========================================
    // NUEVO MÉTODO: MÓDULO 6 (ANULACIONES)
    // ==========================================
    @Transactional
    public Map<String, Object> anularVenta(AnulacionRequest request) {
        // 1. Validar que el usuario tenga caja abierta (Recomendado para auditoría)
        SesionCaja sesion = cajaService.obtenerSesionActual(request.getUsuarioID());
        if (sesion == null) {
            throw new RuntimeException("No puede realizar anulaciones sin una caja abierta.");
        }

        try {
            // Llamamos al SP de anulación lógica
            String sql = "EXEC sp_Operacion_AnularVenta @VentaID = ?, @UsuarioID = ?, @Motivo = ?";
            
            return jdbcTemplate.queryForMap(sql, 
                request.getVentaID(), 
                request.getUsuarioID(), 
                request.getMotivo()
            );

        } catch (Exception e) {
            // Capturamos errores como "La venta no existe" o "Ya está anulada"
            throw new RuntimeException("Error al anular la venta: " + e.getMessage());
        }
    }
}