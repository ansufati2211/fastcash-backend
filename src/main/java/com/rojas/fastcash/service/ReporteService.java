package com.rojas.fastcash.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class ReporteService {

    @Autowired private JdbcTemplate jdbcTemplate;

    public Map<String, Object> obtenerCierreActual(Integer usuarioID) {
        // 1. Buscamos la sesión ABIERTA para saber a qué hora empezó
        String sqlSesion = "SELECT TOP 1 SesionID, SaldoInicial, FechaInicio FROM SesionesCaja WHERE UsuarioID = ? AND Estado = 'ABIERTO'";
        List<Map<String, Object>> sesiones = jdbcTemplate.queryForList(sqlSesion, usuarioID);

        if (sesiones.isEmpty()) {
            // Si no hay caja abierta, devolvemos todo en cero
            return Map.of(
                "Estado", "CERRADO", 
                "Mensaje", "No hay caja abierta",
                "VentasDigital", BigDecimal.ZERO,
                "VentasTarjeta", BigDecimal.ZERO,
                "TotalVendido", BigDecimal.ZERO,
                "SaldoEsperadoEnCaja", BigDecimal.ZERO
            );
        }

        Map<String, Object> sesion = sesiones.get(0);
        BigDecimal saldoInicial = (BigDecimal) sesion.get("SaldoInicial");
        String fechaInicio = sesion.get("FechaInicio").toString();

        // 2. Sumamos las ventas de ESTA sesión manualmente para asegurar precisión
        // Filtramos por Usuario, Fecha >= Inicio Sesión y Estado PAGADO (excluye anulados)
        String sqlVentas = """
            SELECT 
                SUM(CASE WHEN p.FormaPago = 'QR' THEN p.MontoPagado ELSE 0 END) AS VentasYape,
                SUM(CASE WHEN p.FormaPago = 'TARJETA' THEN p.MontoPagado ELSE 0 END) AS VentasTarjeta,
                SUM(CASE WHEN p.FormaPago = 'EFECTIVO' THEN p.MontoPagado ELSE 0 END) AS VentasEfectivo,
                SUM(p.MontoPagado) AS TotalVendido
            FROM PagosRegistrados p
            INNER JOIN Ventas v ON p.VentaID = v.VentaID
            WHERE v.UsuarioID = ? 
              AND v.Estado = 'PAGADO' 
              AND v.FechaEmision >= ? 
        """;

        Map<String, Object> totales = jdbcTemplate.queryForMap(sqlVentas, usuarioID, fechaInicio);

        // Extraer valores con manejo de nulos (si no hubo ventas devuelve null, lo pasamos a 0)
        BigDecimal vYape = totales.get("VentasYape") != null ? (BigDecimal) totales.get("VentasYape") : BigDecimal.ZERO;
        BigDecimal vTarjeta = totales.get("VentasTarjeta") != null ? (BigDecimal) totales.get("VentasTarjeta") : BigDecimal.ZERO;
        BigDecimal vTotal = totales.get("TotalVendido") != null ? (BigDecimal) totales.get("TotalVendido") : BigDecimal.ZERO;

        // 3. Retornamos el mapa con los nombres exactos que usa el Frontend
        return Map.of(
            "Estado", "ABIERTO",
            "FechaApertura", fechaInicio,
            "SaldoInicial", saldoInicial,
            "VentasDigital", vYape,      // YAPE / PLIN
            "VentasTarjeta", vTarjeta,   // TARJETAS
            "TotalVendido", vTotal,      // SUMA TOTAL
            "SaldoEsperadoEnCaja", saldoInicial.add(vTotal) // Saldo Inicial + Ventas
        );
    }
}