package com.rojas.fastcash.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "Turnos")
public class Turno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TurnoID")
    private Integer turnoID;

    @Column(name = "Nombre")
    private String nombre; // MAÑANA, NOCHE

    @Column(name = "HoraInicio")
    private LocalTime horaInicio;

    @Column(name = "HoraFin")
    private LocalTime horaFin;

    @Column(name = "Activo")
    private Boolean activo;
}