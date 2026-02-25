package com.rojas.fastcash.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AperturaCajaRequest {
    @JsonProperty("usuarioID")
    private Integer usuarioID;

    @JsonProperty("saldoInicial")
    private BigDecimal saldoInicial;
}
