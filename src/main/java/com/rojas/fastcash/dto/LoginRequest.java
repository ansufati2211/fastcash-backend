package com.rojas.fastcash.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "El usuario es obligatorio")
    @JsonProperty("username")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @JsonProperty("password")
    private String password;
}
