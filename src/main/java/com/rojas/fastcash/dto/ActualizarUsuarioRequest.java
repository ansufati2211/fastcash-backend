package com.rojas.fastcash.dto;

public class ActualizarUsuarioRequest {
    private Integer usuarioID;
    private String nombreCompleto;
    private String username;
    private Integer rolID;
    private String password; // Opcional (si viene vacío, no se cambia)
    private Boolean activo;  // ¡ESTE ES EL CAMPO QUE FALTABA!

    // --- Getters y Setters Manuales ---

    public Integer getUsuarioID() {
        return usuarioID;
    }

    public void setUsuarioID(Integer usuarioID) {
        this.usuarioID = usuarioID;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getRolID() {
        return rolID;
    }

    public void setRolID(Integer rolID) {
        this.rolID = rolID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // --- Getters y Setters para 'activo' ---

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}