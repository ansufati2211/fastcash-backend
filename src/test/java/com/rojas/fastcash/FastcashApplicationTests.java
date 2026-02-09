package com.rojas.fastcash;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class FastcashApplicationTests {

    @Test
    void contextLoads() {
        // Esta prueba verifica que tu aplicación (Backend + BD) levanta correctamente.
    }

    // 👇 EJECUTA ESTA PRUEBA MANUALMENTE PARA OBTENER TU CONTRASEÑA ENCRIPTADA
    @Test
    void generarHashAdmin() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "r0jasTiend4*";
        String hash = encoder.encode(password);
        
        System.out.println("\n\n========================================");
        System.out.println("PASSWORD PLANO: " + password);
        System.out.println("HASH GENERADO: " + hash);
        System.out.println("========================================\n\n");
    }

}