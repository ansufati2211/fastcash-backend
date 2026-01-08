package com.rojas.fastcash.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CrearUsuarioRequest {
    @NotNull(message = "El ID del administrador es obligatorio")
    private Integer adminID; // Quién está creando al usuario (seguridad)

    @NotBlank(message = "El nombre completo es obligatorio")
    private String nombreCompleto;

    @NotBlank(message = "El username es obligatorio")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @NotNull(message = "El Rol ID es obligatorio (1=Admin, 2=Cajero)")
    private Integer rolID;
}