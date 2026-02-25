package com.rojas.fastcash.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EntidadFinancieraRequest {
    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("tipo")
    private String tipo;

    @JsonProperty("activo")
    private Boolean activo;
}
