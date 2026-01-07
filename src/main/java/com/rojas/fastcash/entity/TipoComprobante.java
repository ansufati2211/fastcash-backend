package com.rojas.fastcash.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "TiposComprobante")
public class TipoComprobante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TipoID")
    private Integer tipoID;

    @Column(name = "CodigoSUNAT")
    private String codigoSUNAT;

    @Column(name = "Nombre")
    private String nombre;

    @Column(name = "Serie")
    private String serie;

    @Column(name = "CorrelativoActual")
    private Integer correlativoActual;

    @Column(name = "RequiereClienteIdentificado")
    private Boolean requiereClienteIdentificado;

    @Column(name = "AfectaIGV")
    private Boolean afectaIGV;
    
    @Column(name = "PermiteAnular")
    private Boolean permiteAnular;
}