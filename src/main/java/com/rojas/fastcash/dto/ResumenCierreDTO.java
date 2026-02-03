package com.rojas.fastcash.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ResumenCierreDTO {
    // Datos de Estado
    private String estado;
    private LocalDateTime fechaApertura;
    private String turnoNombre;         // <--- NUEVO: Retornado por el SP de Postgres

    // Desglose de Dinero
    private BigDecimal saldoInicial;
    private BigDecimal ventasEfectivo;
    private BigDecimal ventasDigital;
    private BigDecimal ventasTarjeta;   // <--- FALTABA: El SP devuelve esto
    
    // Totales
    private BigDecimal totalVendido;
    private BigDecimal saldoEsperadoEnCaja; 
    private BigDecimal totalAnulado;    // <--- NUEVO: Útil para auditoría
}