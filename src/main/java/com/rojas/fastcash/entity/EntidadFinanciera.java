package com.rojas.fastcash.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "EntidadesFinancieras")
public class EntidadFinanciera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EntidadID")
    private Integer entidadID;

    @Column(name = "Nombre")
    private String nombre;

    @Column(name = "Tipo") // 'BANCO', 'BILLETERA'
    private String tipo;

    @Column(name = "Activo")
    private Boolean activo;
}