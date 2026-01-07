package com.rojas.fastcash.service;

import com.rojas.fastcash.entity.*;
import com.rojas.fastcash.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaestrosService {

    @Autowired private CategoriaVentaRepository categoriaRepo;
    @Autowired private EntidadFinancieraRepository entidadRepo;
    @Autowired private TipoComprobanteRepository comprobanteRepo;

    public List<CategoriaVenta> listarCategoriasActivas() {
        return categoriaRepo.findByActivoTrue();
    }

    public List<EntidadFinanciera> listarEntidadesActivas() {
        return entidadRepo.findByActivoTrue();
    }

    public List<TipoComprobante> listarComprobantes() {
        return comprobanteRepo.findAll();
    }
}