package com.rojas.fastcash.repository;

import com.rojas.fastcash.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    // JpaRepository ya tiene findAll(), save(), etc. No necesitas escribir nada más.
}