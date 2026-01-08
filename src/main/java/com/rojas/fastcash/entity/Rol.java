package com.rojas.fastcash.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Roles")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RolID")
    private Integer rolID;

    @Column(name = "Nombre")
    private String nombre;

    @Column(name = "Descripcion")
    private String descripcion;
}