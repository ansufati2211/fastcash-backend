package com.rojas.fastcash.dto;

public class ActualizarUsuarioRequest {

    private Integer usuarioID;
    private String nombreCompleto;
    private String username;
    private Integer rolID;
    private String password;
    
    // ==========================================
    //  ESTOS SON LOS CAMPOS CLAVE
    // ==========================================
    private Integer turnoID; // Debe coincidir con el JSON del front
    private Boolean activo;

    // GETTERS Y SETTERS
    public Integer getUsuarioID() { return usuarioID; }
    public void setUsuarioID(Integer usuarioID) { this.usuarioID = usuarioID; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Integer getRolID() { return rolID; }
    public void setRolID(Integer rolID) { this.rolID = rolID; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Integer getTurnoID() { return turnoID; }
    public void setTurnoID(Integer turnoID) { this.turnoID = turnoID; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}