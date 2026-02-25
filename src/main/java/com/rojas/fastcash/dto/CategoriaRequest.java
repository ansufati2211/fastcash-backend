package com.rojas.fastcash.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CategoriaRequest {
    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("activo")
    private Boolean activo;
}
