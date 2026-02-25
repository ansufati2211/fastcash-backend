package com.rojas.fastcash.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnulacionRequest {
    @NotNull(message = "El ID de la venta es obligatorio")
    @JsonProperty("ventaID")
    private Integer ventaID;

    @NotNull(message = "El usuario que anula es obligatorio")
    @JsonProperty("usuarioID")
    private Integer usuarioID;

    @JsonProperty("motivo")
    private String motivo;
}
