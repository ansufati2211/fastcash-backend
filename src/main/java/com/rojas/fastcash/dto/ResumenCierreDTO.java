package com.rojas.fastcash.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ResumenCierreDTO {
    @JsonProperty("estado")
    private String estado;

    @JsonProperty("fechaApertura")
    private LocalDateTime fechaApertura;

    @JsonProperty("turnoNombre")
    private String turnoNombre;

    @JsonProperty("saldoInicial")
    private BigDecimal saldoInicial;

    @JsonProperty("ventasEfectivo")
    private BigDecimal ventasEfectivo;

    @JsonProperty("ventasDigital")
    private BigDecimal ventasDigital;

    @JsonProperty("ventasTarjeta")
    private BigDecimal ventasTarjeta;

    @JsonProperty("totalVendido")
    private BigDecimal totalVendido;

    @JsonProperty("saldoEsperadoEnCaja")
    private BigDecimal saldoEsperadoEnCaja;

    @JsonProperty("totalAnulado")
    private BigDecimal totalAnulado;
}
