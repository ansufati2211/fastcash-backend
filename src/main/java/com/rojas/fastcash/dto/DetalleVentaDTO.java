package com.rojas.fastcash.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class DetalleVentaDTO {
    // Usamos mayúscula inicial porque SQL Server espera JSON con claves en mayúscula
    @JsonProperty("CategoriaID") 
    private Integer categoriaID;

    @JsonProperty("Monto")
    private BigDecimal monto;
}