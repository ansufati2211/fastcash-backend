package com.rojas.fastcash.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;

@Service
public class ReporteService {

    @Autowired private JdbcTemplate jdbcTemplate;

    // 1. REPORTE GENERAL DE VENTAS
    public List<Map<String, Object>> obtenerReporteVentas(String inicio, String fin, Integer usuarioID) {
        if (inicio == null || inicio.isEmpty()) inicio = LocalDate.now().toString();
        if (fin == null || fin.isEmpty()) fin = LocalDate.now().toString();

        Integer uidParam = (usuarioID != null && usuarioID > 0) ? usuarioID : null;

        // POSTGRES: Casteamos a DATE explícitamente y usamos SELECT * FROM
        String sql = "SELECT * FROM sp_reporte_detalladoventas(?::DATE, ?::DATE, ?, NULL)";
        
        return jdbcTemplate.queryForList(sql, inicio, fin, uidParam);
    }

    // 2. REPORTE POR CAJAS
    public List<Map<String, Object>> obtenerReporteCajas(String inicio, String fin, Integer usuarioID) {
        if (inicio == null || inicio.isEmpty()) inicio = LocalDate.now().toString();
        if (fin == null || fin.isEmpty()) fin = LocalDate.now().toString();
        
        // POSTGRES
        String sql = "SELECT * FROM sp_reporte_porcaja(?::DATE, ?::DATE, ?)";
        return jdbcTemplate.queryForList(sql, inicio, fin, usuarioID);
    }

    // 3. DASHBOARD DE GRÁFICOS
    public Map<String, Object> obtenerDatosGraficos(String fechaStr, Integer usuarioID) {
        String fechaFinal = (fechaStr == null || fechaStr.isEmpty()) ? LocalDate.now().toString() : fechaStr;
        Map<String, Object> resultado = new HashMap<>();
        List<Object> params = new ArrayList<>();
        params.add(fechaFinal); 
        
        String filtroUsuario = "";
        if(usuarioID != null && usuarioID > 0) {
            filtroUsuario = " AND v.UsuarioID = ? ";
            params.add(usuarioID);
        }

        // POSTGRES: 
        // 1. ISNULL -> COALESCE
        // 2. CONVERT(...) -> TO_CHAR(v.FechaEmision, 'YYYY-MM-DD')
        String sqlCat = "SELECT " +
                        "   COALESCE(c.Nombre, 'Sin Categoría') as label, " + 
                        "   COALESCE(SUM(vd.Monto), 0) as value " +
                        "FROM Ventas v " +
                        "LEFT JOIN VentaDetalle vd ON v.VentaID = vd.VentaID " + 
                        "LEFT JOIN CategoriasVenta c ON vd.CategoriaID = c.CategoriaID " +
                        "WHERE TO_CHAR(v.FechaEmision, 'YYYY-MM-DD') = ? " +
                        "  AND v.Estado IN ('PAGADO', 'COMPLETADO') " + 
                        filtroUsuario +
                        "GROUP BY c.Nombre";
        
        String sqlPago = "SELECT " +
                         "   COALESCE(p.FormaPago, 'Sin Pago') as label, " +
                         "   COALESCE(SUM(p.MontoPagado), 0) as value " +
                         "FROM Ventas v " +
                         "LEFT JOIN PagosRegistrados p ON v.VentaID = p.VentaID " +
                         "WHERE TO_CHAR(v.FechaEmision, 'YYYY-MM-DD') = ? " +
                         "  AND v.Estado IN ('PAGADO', 'COMPLETADO') " +
                         filtroUsuario +
                         "GROUP BY p.FormaPago";

        List<Map<String, Object>> catResult = jdbcTemplate.queryForList(sqlCat, params.toArray());
        List<Map<String, Object>> pagoResult = jdbcTemplate.queryForList(sqlPago, params.toArray());

        resultado.put("categorias", forzarMinusculas(catResult));
        resultado.put("pagos", forzarMinusculas(pagoResult));
        
        return resultado;
    }
    
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

    // 4. CIERRE ACTUAL (TICKET)
    public Map<String, Object> obtenerCierreActual(Integer usuarioID) {
        try {
            // POSTGRES
            return jdbcTemplate.queryForMap("SELECT * FROM sp_operacion_obtenercierreactual(?)", usuarioID);
        } catch (Exception e) {
            Map<String, Object> vacio = new HashMap<>();
            vacio.put("SaldoInicial", 0);
            vacio.put("TurnoNombre", "GENERAL"); 
            return vacio;
        }
    }
}