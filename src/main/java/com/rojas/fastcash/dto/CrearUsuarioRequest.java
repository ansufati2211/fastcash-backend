package com.rojas.fastcash.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CrearUsuarioRequest {
    
    private Integer adminId; // Opcional, si no se usa se puede quitar

    @NotBlank(message = "El nombre completo es obligatorio")
    private String nombreCompleto;

    @NotBlank(message = "El username es obligatorio")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @NotNull(message = "El Rol ID es obligatorio (1=Admin, 2=Cajero)")
    private Integer rolId;

    // ✅ NUEVO: Para asignar turno desde la creación
    private Integer turnoId; 
}