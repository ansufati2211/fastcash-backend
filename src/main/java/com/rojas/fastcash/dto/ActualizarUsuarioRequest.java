package com.rojas.fastcash.dto;

import lombok.Data;

@Data
public class ActualizarUsuarioRequest {
    // Usamos 'Id' (camelCase) para evitar problemas de mapeo JSON
    private Integer usuarioId;
    private String nombreCompleto;
    private String username;
    private Integer rolId;
    private String password;
    
    // Campos clave para la asignación de turno y estado
    private Integer turnoId; 
    private Boolean activo;
}