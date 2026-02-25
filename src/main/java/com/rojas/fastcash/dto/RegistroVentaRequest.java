package com.rojas.fastcash.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class RegistroVentaRequest {
    @JsonProperty("usuarioID")
    private Integer usuarioID;

    @JsonProperty("tipoComprobanteID")
    private Integer tipoComprobanteID;

    @JsonProperty("clienteDoc")
    private String clienteDoc;

    @JsonProperty("clienteNombre")
    private String clienteNombre;

    // Si el front no lo envía, llega como null y no pasa nada.
    @JsonProperty("comprobanteExterno")
    private String comprobanteExterno;

    @JsonProperty("detalles")
    private List<DetalleVentaDTO> detalles;

    @JsonProperty("pagos")
    private List<PagoVentaDTO> pagos;
}
