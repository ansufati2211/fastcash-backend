package com.rojas.fastcash.dto;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CierreCajaRequest {
    private Integer usuarioID;
    private BigDecimal saldoFinalReal;
}