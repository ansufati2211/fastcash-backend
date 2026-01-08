package com.rojas.fastcash.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rojas.fastcash.dto.RegistroVentaRequest;
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
    
    // SOLUCIÓN: Lo instanciamos manualmente
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
            // SQL Server espera: '[{"CategoriaID":1, "Monto":10.00}]'
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

            // 4. Ejecutar y capturar la respuesta (El SP devuelve el ID de venta y Número de Ticket)
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
            // Error técnico o de validación SQL
            throw new RuntimeException("Error al procesar la venta: " + e.getMessage());
        }
    }
}