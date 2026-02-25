package com.rojas.fastcash.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ActualizarUsuarioRequest {
    @JsonProperty("usuarioId")
    private Integer usuarioId;

    @JsonProperty("nombreCompleto")
    private String nombreCompleto;

    @JsonProperty("username")
    private String username;

    @JsonProperty("rolId")
    private Integer rolId;

    @JsonProperty("password")
    private String password;

    @JsonProperty("turnoId")
    private Integer turnoId;

    @JsonProperty("activo")
    private Boolean activo;
}
