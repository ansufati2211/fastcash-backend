package com.rojas.fastcash.dto;

import lombok.Data;

@Data
public class EntidadFinancieraRequest {
    private String nombre;
    private String tipo; // "BANCO", "BILLETERA", "OTRO"
    private Boolean activo;
}