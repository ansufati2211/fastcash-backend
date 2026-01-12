package com.rojas.fastcash.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PagoVentaDTO {
    @JsonProperty("FormaPago")  // <--- ESTO ARREGLA LA LECTURA DEL JSON
    private String formaPago;

    @JsonProperty("Monto")
    private BigDecimal monto;

    @JsonProperty("EntidadID")
    private Integer entidadID;

    @JsonProperty("NumOperacion")
    private String numOperacion;
}