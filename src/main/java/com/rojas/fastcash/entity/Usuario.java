package com.rojas.fastcash.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UsuarioID")
    private Integer usuarioID;

    @ManyToOne
    @JoinColumn(name = "RolID")
    private Rol rol;

    @Column(name = "NombreCompleto")
    private String nombreCompleto;

    @Column(name = "Username")
    private String username;

    @Column(name = "Pswd")
    private String password;

    @Column(name = "Activo")
    private Boolean activo;

    @Column(name = "UltimoAcceso")
    private LocalDateTime ultimoAcceso;
}