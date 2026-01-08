package com.rojas.fastcash.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class RegistroVentaRequest {
    @NotNull(message = "El ID del cajero es obligatorio")
    private Integer usuarioID;

    @NotNull(message = "El tipo de comprobante es obligatorio (Factura/Boleta)")
    private Integer tipoComprobanteID; 

    // Opcionales para Boleta (Varios), Obligatorios para Factura
    private String clienteDoc;    // DNI o RUC
    private String clienteNombre; // Razón Social o Nombre

    @NotEmpty(message = "La venta debe tener al menos un detalle (producto/categoría)")
    private List<DetalleVentaDTO> detalles;

    @NotEmpty(message = "Debe indicar al menos una forma de pago")
    private List<PagoVentaDTO> pagos;
}