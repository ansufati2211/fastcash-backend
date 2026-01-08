package com.rojas.fastcash.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AsignarTurnoRequest {
    @NotNull(message = "El ID del administrador es obligatorio")
    private Integer adminID;

    @NotNull(message = "El ID del usuario cajero es obligatorio")
    private Integer usuarioID;

    @NotNull(message = "El ID del turno es obligatorio (1=Mañana, 2=Noche)")
    private Integer turnoID;
}