package com.rojas.fastcash.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PagoVentaDTO {
    @JsonProperty("FormaPago") // 'EFECTIVO', 'QR', 'TARJETA'
    private String formaPago;

    @JsonProperty("Monto")
    private BigDecimal monto;

    @JsonProperty("EntidadID")
    private Integer entidadID; // Banco (BCP, Interbank) o Billetera (Yape, Plin)

    @JsonProperty("NumOperacion")
    private String numOperacion; // El código de operación del voucher/Yape

    @JsonProperty("Lote")
    private String lote; // Para POS físico (Visa/Mastercard)

    @JsonProperty("Ultimos4")
    private String ultimos4; // Últimos dígitos de la tarjeta (Seguridad/Auditoría)
}