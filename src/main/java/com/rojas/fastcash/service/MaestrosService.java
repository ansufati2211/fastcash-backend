package com.rojas.fastcash.service;

import com.rojas.fastcash.dto.CategoriaRequest;
import com.rojas.fastcash.dto.EntidadFinancieraRequest;
import com.rojas.fastcash.entity.CategoriaVenta;
import com.rojas.fastcash.entity.EntidadFinanciera;
import com.rojas.fastcash.entity.Rol;
import com.rojas.fastcash.entity.Turno;
import com.rojas.fastcash.repository.CategoriaVentaRepository;
import com.rojas.fastcash.repository.EntidadFinancieraRepository;
import com.rojas.fastcash.repository.RolRepository;
import com.rojas.fastcash.repository.TurnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaestrosService {

    @Autowired private RolRepository rolRepo;
    @Autowired private TurnoRepository turnoRepo;
    @Autowired private CategoriaVentaRepository categoriaRepo;
    @Autowired private EntidadFinancieraRepository entidadRepo;

    // =======================================================
    // 📖 MÉTODOS DE LECTURA (Lo que ya funcionaba)
    // =======================================================
    public List<Rol> listarRoles() { return rolRepo.findAll(); }
    public List<Turno> listarTurnos() { return turnoRepo.findAll(); }
    public List<CategoriaVenta> listarCategorias() { return categoriaRepo.findAll(); }
    public List<EntidadFinanciera> listarEntidades() { return entidadRepo.findAll(); }

    // =======================================================
    // 📦 GESTIÓN DE CATEGORÍAS (Nuevo)
    // =======================================================

    public CategoriaVenta crearCategoria(CategoriaRequest req) {
        CategoriaVenta nueva = new CategoriaVenta();
        nueva.setNombre(req.getNombre());
        // Validamos que 'activo' no sea null, si lo es, ponemos true por defecto
        nueva.setActivo(req.getActivo() != null ? req.getActivo() : true);
        return categoriaRepo.save(nueva);
    }

    public CategoriaVenta actualizarCategoria(Integer id, CategoriaRequest req) {
        // Validación de nulidad en el ID para evitar warnings
        if (id == null) {
            throw new IllegalArgumentException("El ID de categoría no puede ser nulo");
        }

        CategoriaVenta existente = categoriaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
        
        existente.setNombre(req.getNombre());
        if (req.getActivo() != null) {
            existente.setActivo(req.getActivo());
        }
        return categoriaRepo.save(existente);
    }

    // =======================================================
    // 🏦 GESTIÓN DE ENTIDADES FINANCIERAS (Nuevo)
    // =======================================================

    public EntidadFinanciera crearEntidad(EntidadFinancieraRequest req) {
        EntidadFinanciera nueva = new EntidadFinanciera();
        nueva.setNombre(req.getNombre());
        nueva.setTipo(req.getTipo());
        nueva.setActivo(req.getActivo() != null ? req.getActivo() : true);
        return entidadRepo.save(nueva);
    }

    public EntidadFinanciera actualizarEntidad(Integer id, EntidadFinancieraRequest req) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de entidad no puede ser nulo");
        }

        EntidadFinanciera existente = entidadRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Entidad Financiera no encontrada con ID: " + id));

        existente.setNombre(req.getNombre());
        existente.setTipo(req.getTipo()); // BANCO, BILLETERA, OTRO
        
        if (req.getActivo() != null) {
            existente.setActivo(req.getActivo());
        }
        return entidadRepo.save(existente);
    }
}