package com.rojas.fastcash.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rojas.fastcash.dto.RegistroVentaRequest;
import com.rojas.fastcash.dto.AnulacionRequest;
import com.rojas.fastcash.dto.PagoVentaDTO;
import com.rojas.fastcash.entity.SesionCaja;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Service
public class VentaService {

    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private CajaService cajaService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public Map<String, Object> registrarVenta(RegistroVentaRequest request) {
        
        // 1. REGLA: Caja Abierta
        SesionCaja sesion = cajaService.obtenerSesionActual(request.getUsuarioID());
        if (sesion == null) {
            throw new RuntimeException("CAJA CERRADA: El usuario debe abrir caja antes de vender.");
        }

        // 2. REGLA: Auditoría de Pagos
        if (request.getPagos() != null) {
            for (PagoVentaDTO pago : request.getPagos()) {
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

            // CORRECCIÓN DE HORA: America/Lima
            LocalDateTime fechaPeru = LocalDateTime.now(ZoneId.of("America/Lima"));

            // 4. Llamar al SP
            String sql = "EXEC sp_RegistrarVentaTransaccional " +
                         "@UsuarioID = ?, " +
                         "@TipoComprobanteID = ?, " +
                         "@ClienteDoc = ?, " +
                         "@ClienteNombre = ?, " +
                         "@JsonDetalles = ?, " +
                         "@JsonPagos = ?, " +
                         "@FechaPersonalizada = ?";

            // 5. Ejecutar
            Map<String, Object> resultado = jdbcTemplate.queryForMap(sql,
                    request.getUsuarioID(),
                    request.getTipoComprobanteID(),
                    request.getClienteDoc(),
                    request.getClienteNombre(),
                    jsonDetalles,
                    jsonPagos,
                    fechaPeru 
            );

            if ("ERROR".equals(resultado.get("Status"))) {
                throw new RuntimeException((String) resultado.get("Mensaje"));
            }

            return resultado;

        } catch (Exception e) {
            String errorMsg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            throw new RuntimeException("Error al procesar venta: " + errorMsg);
        }
    }

    @Transactional
    public Map<String, Object> anularVenta(AnulacionRequest request) {
        SesionCaja sesion = cajaService.obtenerSesionActual(request.getUsuarioID());
        if (sesion == null) {
            throw new RuntimeException("No puede realizar anulaciones sin una caja abierta.");
        }

        try {
            String sql = "EXEC sp_Operacion_AnularVenta @VentaID = ?, @UsuarioID = ?, @Motivo = ?";
            return jdbcTemplate.queryForMap(sql, request.getVentaID(), request.getUsuarioID(), request.getMotivo());
        } catch (Exception e) {
            String errorMsg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            throw new RuntimeException("Error al anular: " + errorMsg);
        }
    }

    // ==========================================
    // MÉTODO: LISTAR HISTORIAL (CAPACIDAD 70)
    // ==========================================
    public List<Map<String, Object>> listarHistorialDia(Integer usuarioID) {
        // CAMBIO AQUÍ: Se cambió TOP 50 por TOP 70
        String sql = """
            SELECT TOP 70 
                v.VentaID,
                tc.Nombre AS TipoComprobante,
                (v.SerieComprobante + '-' + v.NumeroComprobante) AS RefOperacion,
                v.ImporteTotal,
                v.FechaEmision,
                v.Estado,
                p.FormaPago,
                p.NumeroOperacion AS CodigoPago,
                p.EntidadFinancieraID,
                (SELECT TOP 1 cv.Nombre 
                 FROM VentaDetalle vd 
                 INNER JOIN CategoriasVenta cv ON vd.CategoriaID = cv.CategoriaID 
                 WHERE vd.VentaID = v.VentaID) AS Familia
            FROM Ventas v
            INNER JOIN TiposComprobante tc ON v.TipoComprobanteID = tc.TipoID
            INNER JOIN PagosRegistrados p ON v.VentaID = p.VentaID
            WHERE v.UsuarioID = ? 
            -- SIN FILTRO DE FECHA PARA TUS PRUEBAS (En producción descomentas la línea de abajo)
            -- AND CAST(v.FechaEmision AS DATE) = CAST(GETDATE() AS DATE)
            ORDER BY v.FechaEmision DESC
        """;

        try {
            return jdbcTemplate.queryForList(sql, usuarioID);
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar historial: " + e.getMessage());
        }
    }
}