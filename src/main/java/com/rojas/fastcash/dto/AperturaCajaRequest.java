package com.rojas.fastcash.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AperturaCajaRequest {
    @NotNull(message = "El usuario es obligatorio")
    private Integer usuarioID;

    @NotNull(message = "El saldo inicial es obligatorio")
    @PositiveOrZero(message = "El saldo no puede ser negativo")
    private BigDecimal saldoInicial;
}