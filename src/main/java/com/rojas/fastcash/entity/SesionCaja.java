package com.rojas.fastcash.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "SesionesCaja")
public class SesionCaja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SesionID")
    private Integer sesionID;

    @ManyToOne
    @JoinColumn(name = "UsuarioID")
    private Usuario usuario;

    @Column(name = "TurnoID")
    private Integer turnoID; // Guardamos el ID del horario referencial

    @Column(name = "FechaInicio")
    private LocalDateTime fechaInicio;

    @Column(name = "FechaCierre")
    private LocalDateTime fechaCierre;

    @Column(name = "SaldoInicial")
    private BigDecimal saldoInicial;

    @Column(name = "SaldoFinal")
    private BigDecimal saldoFinal;

    @Column(name = "Estado")
    private String estado;
}