package com.rojas.fastcash.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;

@Service
public class ReporteService {

    @Autowired private JdbcTemplate jdbcTemplate;

    // ==========================================
    // 1. REPORTE GENERAL DE VENTAS
    // ==========================================
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
           // Comparación segura por texto YYYY-MM-DD
           .append("WHERE CAST(v.FechaEmision AS DATE) >= CAST(? AS DATE) AND CAST(v.FechaEmision AS DATE) <= CAST(? AS DATE) ");
        
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

    // ==========================================
    // 2. REPORTE POR CAJAS
    // ==========================================
    public List<Map<String, Object>> obtenerReporteCajas(String inicio, String fin, Integer usuarioID) {
        if (inicio == null || inicio.isEmpty()) inicio = LocalDate.now().toString();
        if (fin == null || fin.isEmpty()) fin = LocalDate.now().toString();
        return jdbcTemplate.queryForList("EXEC sp_Reporte_PorCaja ?, ?, ?", inicio, fin, usuarioID);
    }

    // ================================================================
    // 3. DASHBOARD DE GRÁFICOS (VERSIÓN TEXTO PURO) 🛡️
    // ================================================================
    public Map<String, Object> obtenerDatosGraficos(String fechaStr, Integer usuarioID) {
        // Usamos la fecha como String simple "yyyy-MM-dd"
        String fechaFinal = (fechaStr == null || fechaStr.isEmpty()) ? LocalDate.now().toString() : fechaStr;

        System.out.println("⚡ GRAFICOS REQUEST -> Buscando fecha String: [" + fechaFinal + "]");

        Map<String, Object> resultado = new HashMap<>();
        List<Object> params = new ArrayList<>();
        params.add(fechaFinal); 
        
        // Filtro de usuario opcional
        String filtroUsuario = "";
        if(usuarioID != null && usuarioID > 0) {
            filtroUsuario = " AND v.UsuarioID = ? ";
            params.add(usuarioID);
        }

        // SQL 1: Categorías 
        // TRUCO: CONVERT(VARCHAR(10), v.FechaEmision, 120) convierte la fecha a "yyyy-MM-dd"
        // Así comparamos Texto vs Texto. ¡No falla nunca!
        String sqlCat = "SELECT " +
                        "   ISNULL(c.Nombre, 'Sin Categoría') as label, " + 
                        "   ISNULL(SUM(vd.Monto), 0) as value " +
                        "FROM Ventas v " +
                        "LEFT JOIN VentaDetalle vd ON v.VentaID = vd.VentaID " + 
                        "LEFT JOIN CategoriasVenta c ON vd.CategoriaID = c.CategoriaID " +
                        "WHERE CONVERT(VARCHAR(10), v.FechaEmision, 120) = ? " +
                        "  AND v.Estado IN ('PAGADO', 'COMPLETADO') " + 
                        filtroUsuario +
                        "GROUP BY c.Nombre";
        
        // SQL 2: Medios de Pago
        String sqlPago = "SELECT " +
                         "   ISNULL(p.FormaPago, 'Sin Pago') as label, " +
                         "   ISNULL(SUM(p.MontoPagado), 0) as value " +
                         "FROM Ventas v " +
                         "LEFT JOIN PagosRegistrados p ON v.VentaID = p.VentaID " +
                         "WHERE CONVERT(VARCHAR(10), v.FechaEmision, 120) = ? " +
                         "  AND v.Estado IN ('PAGADO', 'COMPLETADO') " +
                         filtroUsuario +
                         "GROUP BY p.FormaPago";

        List<Map<String, Object>> catResult = jdbcTemplate.queryForList(sqlCat, params.toArray());
        List<Map<String, Object>> pagoResult = jdbcTemplate.queryForList(sqlPago, params.toArray());

        System.out.println("   -> Resultados Categorias: " + catResult.size()); // Mira tu consola, debe ser > 0
        System.out.println("   -> Resultados Pagos: " + pagoResult.size());     // Mira tu consola, debe ser > 0

        resultado.put("categorias", forzarMinusculas(catResult));
        resultado.put("pagos", forzarMinusculas(pagoResult));
        
        return resultado;
    }
    
    // --- FUNCIÓN AUXILIAR (Vital para que el Frontend entienda 'label' y 'value') ---
    private List<Map<String, Object>> forzarMinusculas(List<Map<String, Object>> listaOriginal) {
        List<Map<String, Object>> listaLimpia = new ArrayList<>();
        for (Map<String, Object> fila : listaOriginal) {
            Map<String, Object> mapaLimpio = new HashMap<>();
            Object label = null;
            Object value = null;
            
            for (String key : fila.keySet()) {
                if (key.equalsIgnoreCase("label")) label = fila.get(key);
                if (key.equalsIgnoreCase("value")) value = fila.get(key);
            }
            if (label != null) mapaLimpio.put("label", label);
            if (value != null) mapaLimpio.put("value", value);
            
            if (!mapaLimpio.isEmpty()) listaLimpia.add(mapaLimpio);
        }
        return listaLimpia;
    }

    // ==========================================
    // 4. CIERRE ACTUAL
    // ==========================================
    public Map<String, Object> obtenerCierreActual(Integer usuarioID) {
        try {
            return jdbcTemplate.queryForMap("EXEC sp_Operacion_ObtenerCierreActual ?", usuarioID);
        } catch (Exception e) {
            // Manejo silencioso: si no hay caja abierta, retorna 0
            Map<String, Object> vacio = new HashMap<>();
            vacio.put("SaldoInicial", 0);
            vacio.put("VentasEfectivo", 0);
            vacio.put("VentasDigital", 0);
            vacio.put("VentasTarjeta", 0);
            vacio.put("TotalVendido", 0);
            vacio.put("SaldoEsperadoEnCaja", 0);
            return vacio;
        }
    }
}