package com.rojas.fastcash.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class DetalleVentaDTO {
    @JsonProperty("CategoriaID")
    private Integer categoriaID;

    @JsonProperty("Monto")
    private BigDecimal monto;
}