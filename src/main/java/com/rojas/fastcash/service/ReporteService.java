package com.rojas.fastcash.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;

@Service
public class ReporteService {

    @Autowired private JdbcTemplate jdbcTemplate;

    // 1. REPORTE GENERAL DE VENTAS (Con filtros dinámicos)
    public List<Map<String, Object>> obtenerReporteVentas(String inicio, String fin, Integer usuarioID) {
        if (inicio == null || inicio.isEmpty()) inicio = LocalDate.now().toString();
        if (fin == null || fin.isEmpty()) fin = LocalDate.now().toString();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT v.VentaID, u.NombreCompleto as Cajero, ")
           .append("CONCAT(v.SerieComprobante, '-', v.NumeroComprobante) as Ticket, ")
           .append("tc.Nombre as TipoDoc, ")
           .append("v.ImporteTotal, FORMAT(v.FechaEmision, 'dd/MM/yyyy HH:mm') as Fecha, v.Estado ")
           .append("FROM Ventas v ")
           .append("JOIN Usuarios u ON v.UsuarioID = u.UsuarioID ")
           .append("JOIN TiposComprobante tc ON v.TipoComprobanteID = tc.TipoID ")
           .append("WHERE CAST(v.FechaEmision AS DATE) BETWEEN ? AND ? ");
        
        List<Object> params = new ArrayList<>();
        params.add(inicio);
        params.add(fin);

        if (usuarioID != null && usuarioID > 0) {
            sql.append(" AND v.UsuarioID = ? ");
            params.add(usuarioID);
        }

        sql.append(" ORDER BY v.VentaID DESC");
        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }

    // 2. REPORTE POR CAJAS (Sesiones)
    public List<Map<String, Object>> obtenerReporteCajas(String inicio, String fin, Integer usuarioID) {
        if (inicio == null || inicio.isEmpty()) inicio = LocalDate.now().toString();
        if (fin == null || fin.isEmpty()) fin = LocalDate.now().toString();
        
        // Llama al SP corregido que usa 'SaldoFinal'
        return jdbcTemplate.queryForList("EXEC sp_Reporte_PorCaja ?, ?, ?", inicio, fin, usuarioID);
    }

    // 3. DASHBOARD DE GRÁFICOS (Limpiamos la variable sqlBase que daba error)
    public Map<String, Object> obtenerDatosGraficos(String fecha, Integer usuarioID) {
        if (fecha == null || fecha.isEmpty()) fecha = LocalDate.now().toString();
        
        Map<String, Object> resultado = new HashMap<>();
        
        // Preparamos parámetros
        List<Object> params = new ArrayList<>();
        params.add(fecha);
        if(usuarioID != null && usuarioID > 0) params.add(usuarioID);

        // SQL 1: Por Categoría (Pastel)
        String sqlCat = "SELECT c.Nombre as label, SUM(vd.Monto) as value FROM Ventas v " +
                        "JOIN VentaDetalle vd ON v.VentaID = vd.VentaID " +
                        "JOIN CategoriasVenta c ON vd.CategoriaID = c.CategoriaID " +
                        "WHERE CAST(v.FechaEmision AS DATE) = ? AND v.Estado = 'PAGADO' " +
                        (usuarioID != null && usuarioID > 0 ? "AND v.UsuarioID = ? " : "") +
                        "GROUP BY c.Nombre";
        
        // SQL 2: Por Medio de Pago (Barras)
        String sqlPago = "SELECT p.FormaPago as label, SUM(p.MontoPagado) as value FROM Ventas v " +
                         "JOIN PagosRegistrados p ON v.VentaID = p.VentaID " +
                         "WHERE CAST(v.FechaEmision AS DATE) = ? AND v.Estado = 'PAGADO' " +
                         (usuarioID != null && usuarioID > 0 ? "AND v.UsuarioID = ? " : "") +
                         "GROUP BY p.FormaPago";

        resultado.put("categorias", jdbcTemplate.queryForList(sqlCat, params.toArray()));
        resultado.put("pagos", jdbcTemplate.queryForList(sqlPago, params.toArray()));
        
        return resultado;
    }
    
    // 4. CIERRE ACTUAL (Este era el método "undefined" que faltaba)
    public Map<String, Object> obtenerCierreActual(Integer usuarioID) {
        try {
            // Usa el SP robusto 'sp_Operacion_ObtenerCierreActual'
            return jdbcTemplate.queryForMap("EXEC sp_Operacion_ObtenerCierreActual ?", usuarioID);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("Estado", "ERROR");
            error.put("TotalVendido", 0);
            return error;
        }
    }
}