package com.rojas.fastcash.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnulacionRequest {
    @NotNull(message = "El ID de la venta es obligatorio")
    private Integer ventaID;

    @NotNull(message = "El usuario que anula es obligatorio")
    private Integer usuarioID;

    private String motivo; // Opcional
}