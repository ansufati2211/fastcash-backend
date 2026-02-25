package com.rojas.fastcash.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CrearUsuarioRequest {
    @JsonProperty("adminId")
    private Integer adminId;

    @NotBlank(message = "El nombre completo es obligatorio")
    @JsonProperty("nombreCompleto")
    private String nombreCompleto;

    @NotBlank(message = "El username es obligatorio")
    @JsonProperty("username")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @JsonProperty("password")
    private String password;

    @NotNull(message = "El Rol ID es obligatorio")
    @JsonProperty("rolId")
    private Integer rolId;

    @JsonProperty("turnoId")
    private Integer turnoId;
}
