package com.rojas.fastcash.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AsignarTurnoRequest {
    @NotNull(message = "El ID del administrador es obligatorio")
    @JsonProperty("adminID")
    private Integer adminID;

    @NotNull(message = "El ID del usuario cajero es obligatorio")
    @JsonProperty("usuarioID")
    private Integer usuarioID;

    @NotNull(message = "El ID del turno es obligatorio")
    @JsonProperty("turnoID")
    private Integer turnoID;
}
