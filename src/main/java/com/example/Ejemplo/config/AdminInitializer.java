        package com.example.Ejemplo.config;

import com.example.Ejemplo.models.Rol;
import com.example.Ejemplo.models.RolEntity;
import com.example.Ejemplo.models.Usuario;
import com.example.Ejemplo.repository.RolEntityRepository;
import com.example.Ejemplo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Order(2) // Se ejecuta después de RolesPermisosInitializer
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolEntityRepository rolEntityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String correoAdmin = "admin@cafeteria.com";

        // Verifica si ya existe
        Optional<Usuario> adminExistente = usuarioRepository.findByCorreo(correoAdmin);
        if (adminExistente.isEmpty()) {
            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setCorreo(correoAdmin);
            admin.setPassword(passwordEncoder.encode("admin123")); // Puedes cambiarla luego
            admin.setRol(Rol.ADMINISTRADOR); // Mantener para compatibilidad
            admin.setFechaIngreso(LocalDateTime.now());
            admin.setEstado(true);
            
            // Asignar el nuevo rol de entidad
            Optional<RolEntity> rolAdmin = rolEntityRepository.findByNombre("ADMINISTRADOR");
            rolAdmin.ifPresent(admin::setRolEntity);

            usuarioRepository.save(admin);
            System.out.println("✅ Usuario ADMINISTRADOR creado con éxito.");
        } else {
            // Actualizar usuarios existentes con el nuevo sistema de roles
            Usuario admin = adminExistente.get();
            if (admin.getRolEntity() == null && admin.getRol() != null) {
                Optional<RolEntity> rolEntity = rolEntityRepository.findByNombre(admin.getRol().name());
                if (rolEntity.isPresent()) {
                    admin.setRolEntity(rolEntity.get());
                    usuarioRepository.save(admin);
                    System.out.println("✅ Usuario ADMINISTRADOR actualizado con nuevo sistema de roles.");
                }
            } else {
                System.out.println("ℹ️ Usuario ADMINISTRADOR ya existe.");
            }
        }
    }
}
