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
    // 1. REPORTE GENERAL DE VENTAS (MEJORADO CON COLUMNAS EXTRA)
    // ==========================================
    public List<Map<String, Object>> obtenerReporteVentas(String inicio, String fin, Integer usuarioID) {
        // Valores por defecto si vienen vacíos
        if (inicio == null || inicio.isEmpty()) inicio = LocalDate.now().toString();
        if (fin == null || fin.isEmpty()) fin = LocalDate.now().toString();

        // Si usuarioID es 0 o negativo, lo mandamos como NULL al SP para que traiga todo
        Integer uidParam = (usuarioID != null && usuarioID > 0) ? usuarioID : null;

        // Llamamos al SP 'sp_Reporte_DetalladoVentas' que ya incluye:
        // [Nro Operacion / Lote] y [Ticket Fisico] (Boleta Manual)
        // El último parámetro es el MetodoPago (NULL = todos)
        String sql = "EXEC sp_Reporte_DetalladoVentas ?, ?, ?, NULL";
        
        return jdbcTemplate.queryForList(sql, inicio, fin, uidParam);
    }

    // ==========================================
    // 2. REPORTE POR CAJAS
    // ==========================================
    public List<Map<String, Object>> obtenerReporteCajas(String inicio, String fin, Integer usuarioID) {
        if (inicio == null || inicio.isEmpty()) inicio = LocalDate.now().toString();
        if (fin == null || fin.isEmpty()) fin = LocalDate.now().toString();
        
        // Llamada al SP existente
        return jdbcTemplate.queryForList("EXEC sp_Reporte_PorCaja ?, ?, ?", inicio, fin, usuarioID);
    }

    // ================================================================
    // 3. DASHBOARD DE GRÁFICOS (VERSIÓN ROBUSTA) 🛡️
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

        // Ejecutamos las consultas con la misma lista de parámetros (reutilizada)
        List<Map<String, Object>> catResult = jdbcTemplate.queryForList(sqlCat, params.toArray());
        List<Map<String, Object>> pagoResult = jdbcTemplate.queryForList(sqlPago, params.toArray());

        System.out.println("   -> Resultados Categorias: " + catResult.size());
        System.out.println("   -> Resultados Pagos: " + pagoResult.size());

        // Limpiamos los nombres de las claves para que el JS no falle (label/value en minúscula)
        resultado.put("categorias", forzarMinusculas(catResult));
        resultado.put("pagos", forzarMinusculas(pagoResult));
        
        return resultado;
    }
    
    // --- FUNCIÓN AUXILIAR (Vital para los gráficos) ---
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
    // 4. CIERRE ACTUAL (TICKET)
    // ==========================================
    public Map<String, Object> obtenerCierreActual(Integer usuarioID) {
        try {
            // Este SP ya fue actualizado para traer el TurnoNombre
            return jdbcTemplate.queryForMap("EXEC sp_Operacion_ObtenerCierreActual ?", usuarioID);
        } catch (Exception e) {
            // Manejo silencioso: si no hay caja abierta, retorna vacíos para no romper el front
            Map<String, Object> vacio = new HashMap<>();
            vacio.put("SaldoInicial", 0);
            vacio.put("VentasEfectivo", 0);
            vacio.put("VentasDigital", 0);
            vacio.put("VentasTarjeta", 0);
            vacio.put("TotalVendido", 0);
            vacio.put("SaldoEsperadoEnCaja", 0);
            vacio.put("TurnoNombre", "GENERAL"); 
            return vacio;
        }
    }
}