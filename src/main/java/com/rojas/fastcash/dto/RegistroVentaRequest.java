package com.rojas.fastcash.dto;
import lombok.Data;
import java.util.List;

@Data
public class RegistroVentaRequest {
    private Integer usuarioID;
    private Integer tipoComprobanteID;
    private String clienteDoc;
    private String clienteNombre;

    // --- NUEVO CAMPO (OPCIONAL) ---
    // Si el front no lo envía, llega como null y no pasa nada.
    private String comprobanteExterno; 
    // ------------------------------

    private List<DetalleVentaDTO> detalles;
    private List<PagoVentaDTO> pagos;
}