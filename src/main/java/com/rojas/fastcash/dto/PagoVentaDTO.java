package com.rojas.fastcash.dto;

import com.fasterxml.jackson.annotation.JsonProperty; // <--- IMPORTANTE
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PagoVentaDTO {
    
    @JsonProperty("FormaPago")  // Mapea exactamente con SQL y Postman
    private String formaPago;

    @JsonProperty("Monto")
    private BigDecimal monto;

    @JsonProperty("EntidadID")
    private Integer entidadID;

    @JsonProperty("NumOperacion")
    private String numOperacion;
}