package com.rojas.fastcash.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "CategoriasVenta")
public class CategoriaVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CategoriaID")
    private Integer categoriaID;

    @Column(name = "Nombre")
    private String nombre;

    @Column(name = "Activo")
    private Boolean activo;
}