package com.rojas.fastcash.dto;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RegistroVentaRequest {
    private Integer usuarioID;
    private Integer tipoComprobanteID;
    private String clienteDoc;
    private String clienteNombre;
    
    private LocalDateTime fechaEmision; // Fecha Manual
    private String numeroComprobanteManual; // <--- EL NUEVO CAMPO (Opcional)
    
    private List<DetalleVentaDTO> detalles;
    private List<PagoVentaDTO> pagos;
}