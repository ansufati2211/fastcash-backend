package com.rojas.fastcash.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ResumenCierreDTO {
    private String estado;
    private LocalDateTime fechaApertura;
    private BigDecimal saldoInicial;
    private BigDecimal ventasEfectivo;
    private BigDecimal ventasDigital;
    private BigDecimal totalVendido;
    private BigDecimal saldoEsperadoEnCaja; // Lo que debe haber físicamente
}