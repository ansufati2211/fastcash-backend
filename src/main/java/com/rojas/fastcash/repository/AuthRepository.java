package com.rojas.fastcash.repository;

import com.rojas.fastcash.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// ⚠️ CAMBIO IMPORTANTE: Ahora es 'interface', no 'class'
// Extiende de JpaRepository para tener métodos gratis: save, findAll, delete, etc.
@Repository
public interface AuthRepository extends JpaRepository<Usuario, Integer> {

    // Spring Data JPA implementa esto automáticamente por el nombre
    // SELECT * FROM Usuarios WHERE Username = ?
    Usuario findByUsername(String username);

}