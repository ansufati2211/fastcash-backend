package com.rojas.fastcash.service;

import com.rojas.fastcash.entity.CategoriaVenta;
import com.rojas.fastcash.entity.EntidadFinanciera;
import com.rojas.fastcash.entity.TipoComprobante;
import com.rojas.fastcash.repository.CategoriaVentaRepository;
import com.rojas.fastcash.repository.EntidadFinancieraRepository;
import com.rojas.fastcash.repository.TipoComprobanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MaestrosService {

    @Autowired private CategoriaVentaRepository categoriaRepo;
    @Autowired private EntidadFinancieraRepository entidadRepo;
    @Autowired private TipoComprobanteRepository tipoRepo;

    // 1. Categorías (Usamos findByActivoTrue para filtrar inactivos)
    public List<CategoriaVenta> listarCategoriasActivas() {
        return categoriaRepo.findByActivoTrue();
    }

    // 2. Entidades (Usamos findByActivoTrue)
    public List<EntidadFinanciera> listarEntidadesActivas() {
        return entidadRepo.findByActivoTrue();
    }

    // 3. Comprobantes (Renombrado para coincidir con el Controlador)
    public List<TipoComprobante> listarComprobantes() {
        return tipoRepo.findAll();
    }
}