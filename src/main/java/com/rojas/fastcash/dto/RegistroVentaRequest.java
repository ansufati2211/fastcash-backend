package com.rojas.fastcash.dto;
import lombok.Data;
import java.util.List;

@Data
public class RegistroVentaRequest {
    private Integer usuarioID;
    private Integer tipoComprobanteID;
    private String clienteDoc;
    private String clienteNombre;
    
    // SE ELIMINARON FECHA Y TICKET MANUAL

    private List<DetalleVentaDTO> detalles;
    private List<PagoVentaDTO> pagos;
}