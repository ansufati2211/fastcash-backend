package com.rojas.fastcash.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rojas.fastcash.dto.AnulacionRequest;
import com.rojas.fastcash.dto.PagoVentaDTO;
import com.rojas.fastcash.dto.RegistroVentaRequest;
import com.rojas.fastcash.entity.SesionCaja;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class VentaService {

    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private CajaService cajaService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public Map<String, Object> registrarVenta(RegistroVentaRequest request) {
        SesionCaja sesion = cajaService.obtenerSesionActual(request.getUsuarioID());
        if (sesion == null) throw new RuntimeException("CAJA CERRADA: Debe abrir caja antes de vender.");

        if (request.getPagos() != null) {
            for (PagoVentaDTO pago : request.getPagos()) {
                if (!"EFECTIVO".equals(pago.getFormaPago())) {
                    if (pago.getNumOperacion() == null || pago.getNumOperacion().trim().isEmpty()) {
                        throw new RuntimeException("ERROR: Ingrese N° Operación para " + pago.getFormaPago());
                    }
                }
            }
        }

        try {
            // POSTGRES: Cast ?::json
            String sql = "SELECT * FROM sp_ventas_registrar(?, ?, ?, ?, ?::json, ?::json, ?)";
            
            return jdbcTemplate.queryForMap(sql,
                    request.getUsuarioID(),
                    request.getTipoComprobanteID(),
                    request.getClienteDoc(),
                    request.getClienteNombre(),
                    objectMapper.writeValueAsString(request.getDetalles()),
                    objectMapper.writeValueAsString(request.getPagos()),
                    request.getComprobanteExterno()
            );

        } catch (Exception e) {
            String msg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            if (msg.contains("ERROR:")) msg = msg.substring(msg.indexOf("ERROR:") + 7).split("\n")[0];
            throw new RuntimeException("Error: " + msg);
        }
    }

    public List<Map<String, Object>> listarHistorialDia(Integer usuarioID, Integer filtroUsuarioID) {
        String sql = "SELECT * FROM sp_historialventas_filtrado(?, ?)";
        return jdbcTemplate.queryForList(sql, usuarioID, filtroUsuarioID);
    }

    @Transactional
    public Map<String, Object> anularVenta(AnulacionRequest request) {
        String sql = "SELECT * FROM sp_operacion_anularventa(?, ?, ?)";
        return jdbcTemplate.queryForMap(sql, request.getVentaID(), request.getUsuarioID(), request.getMotivo());
    }
}