package com.rojas.fastcash.dto;

import lombok.Data;

@Data
public class CategoriaRequest {
    private String nombre;
    private Boolean activo; // Para poder activar/desactivar
}