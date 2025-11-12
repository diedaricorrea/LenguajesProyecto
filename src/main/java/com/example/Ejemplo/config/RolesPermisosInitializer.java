package com.example.Ejemplo.config;

import com.example.Ejemplo.models.Permiso;
import com.example.Ejemplo.models.RolEntity;
import com.example.Ejemplo.repository.PermisoRepository;
import com.example.Ejemplo.repository.RolEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Inicializador de roles y permisos del sistema
 * Se ejecuta al iniciar la aplicación para crear los datos base
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class RolesPermisosInitializer implements CommandLineRunner {

    private final PermisoRepository permisoRepository;
    private final RolEntityRepository rolEntityRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Inicializando roles y permisos del sistema...");
        
        // Crear permisos si no existen
        crearPermisosSiNoExisten();
        
        // Crear roles con sus permisos
        crearRolesSiNoExisten();
        
        log.info("Roles y permisos inicializados correctamente");
    }

    private void crearPermisosSiNoExisten() {
        List<Permiso> permisos = Arrays.asList(
            // PRODUCTOS
            new Permiso("PRODUCTOS_VER", "Ver catálogo de productos", "PRODUCTOS"),
            new Permiso("PRODUCTOS_CREAR", "Crear nuevos productos", "PRODUCTOS"),
            new Permiso("PRODUCTOS_EDITAR", "Editar productos existentes", "PRODUCTOS"),
            new Permiso("PRODUCTOS_ELIMINAR", "Eliminar productos", "PRODUCTOS"),
            new Permiso("PRODUCTOS_GESTIONAR", "Gestión completa de productos", "PRODUCTOS"),
            
            // CATEGORÍAS
            new Permiso("CATEGORIAS_VER", "Ver categorías", "CATEGORIAS"),
            new Permiso("CATEGORIAS_CREAR", "Crear nuevas categorías", "CATEGORIAS"),
            new Permiso("CATEGORIAS_EDITAR", "Editar categorías", "CATEGORIAS"),
            new Permiso("CATEGORIAS_ELIMINAR", "Eliminar categorías", "CATEGORIAS"),
            
            // PEDIDOS
            new Permiso("PEDIDOS_VER", "Ver pedidos", "PEDIDOS"),
            new Permiso("PEDIDOS_CREAR", "Crear pedidos", "PEDIDOS"),
            new Permiso("PEDIDOS_GESTIONAR", "Gestionar todos los pedidos", "PEDIDOS"),
            new Permiso("PEDIDOS_ACTUALIZAR_ESTADO", "Actualizar estado de pedidos", "PEDIDOS"),
            
            // USUARIOS
            new Permiso("USUARIOS_VER", "Ver lista de usuarios", "USUARIOS"),
            new Permiso("USUARIOS_CREAR", "Crear nuevos usuarios", "USUARIOS"),
            new Permiso("USUARIOS_EDITAR", "Editar usuarios", "USUARIOS"),
            new Permiso("USUARIOS_ELIMINAR", "Eliminar usuarios", "USUARIOS"),
            new Permiso("USUARIOS_GESTIONAR_ROLES", "Asignar roles a usuarios", "USUARIOS"),
            
            // VENTAS
            new Permiso("VENTAS_VER", "Ver registro de ventas", "VENTAS"),
            new Permiso("VENTAS_CREAR", "Registrar ventas", "VENTAS"),
            new Permiso("VENTAS_GESTIONAR", "Gestión completa de ventas", "VENTAS"),
            
            // ESTADÍSTICAS
            new Permiso("ESTADISTICAS_VER", "Ver estadísticas del sistema", "ESTADISTICAS"),
            new Permiso("ESTADISTICAS_PRODUCTOS", "Ver estadísticas de productos", "ESTADISTICAS"),
            new Permiso("ESTADISTICAS_VENTAS", "Ver estadísticas de ventas", "ESTADISTICAS"),
            
            // NOTIFICACIONES
            new Permiso("NOTIFICACIONES_VER", "Ver notificaciones", "NOTIFICACIONES"),
            new Permiso("NOTIFICACIONES_CREAR", "Crear notificaciones", "NOTIFICACIONES"),
            new Permiso("NOTIFICACIONES_GESTIONAR", "Gestionar notificaciones", "NOTIFICACIONES"),
            
            // MENÚ DEL DÍA
            new Permiso("MENU_DIA_VER", "Ver menú del día", "MENU_DIA"),
            new Permiso("MENU_DIA_GESTIONAR", "Gestionar menú del día", "MENU_DIA"),
            
            // CARRITO
            new Permiso("CARRITO_GESTIONAR", "Gestionar carrito de compras", "CARRITO")
        );

        for (Permiso permiso : permisos) {
            if (!permisoRepository.existsByNombre(permiso.getNombre())) {
                permisoRepository.save(permiso);
                log.debug("Permiso creado: {}", permiso.getNombre());
            }
        }
    }

    private void crearRolesSiNoExisten() {
        // ROL ADMINISTRADOR - Acceso total
        if (!rolEntityRepository.existsByNombre("ADMINISTRADOR")) {
            RolEntity admin = new RolEntity("ADMINISTRADOR", "Administrador del sistema con acceso total");
            
            // El administrador tiene TODOS los permisos
            List<Permiso> todosPermisos = permisoRepository.findAll();
            todosPermisos.forEach(admin::agregarPermiso);
            
            rolEntityRepository.save(admin);
            log.info("Rol ADMINISTRADOR creado con {} permisos", todosPermisos.size());
        }

        // ROL TRABAJADOR - Gestión operativa
        if (!rolEntityRepository.existsByNombre("TRABAJADOR")) {
            RolEntity trabajador = new RolEntity("TRABAJADOR", "Trabajador con permisos operativos");
            
            List<String> permisosTrabjador = Arrays.asList(
                "PRODUCTOS_VER", "PRODUCTOS_CREAR", "PRODUCTOS_EDITAR",
                "CATEGORIAS_VER", "CATEGORIAS_CREAR", "CATEGORIAS_EDITAR",
                "PEDIDOS_VER", "PEDIDOS_GESTIONAR", "PEDIDOS_ACTUALIZAR_ESTADO",
                "VENTAS_VER", "VENTAS_CREAR",
                "ESTADISTICAS_VER", "ESTADISTICAS_PRODUCTOS", "ESTADISTICAS_VENTAS",
                "NOTIFICACIONES_VER", "NOTIFICACIONES_CREAR", "NOTIFICACIONES_GESTIONAR",
                "MENU_DIA_VER", "MENU_DIA_GESTIONAR",
                "CARRITO_GESTIONAR"
            );
            
            permisosTrabjador.forEach(nombrePermiso -> {
                permisoRepository.findByNombre(nombrePermiso)
                    .ifPresent(trabajador::agregarPermiso);
            });
            
            rolEntityRepository.save(trabajador);
            log.info("Rol TRABAJADOR creado con {} permisos", trabajador.getPermisos().size());
        }

        // ROL USUARIO - Permisos básicos
        if (!rolEntityRepository.existsByNombre("USUARIO")) {
            RolEntity usuario = new RolEntity("USUARIO", "Usuario regular del sistema");
            
            List<String> permisosUsuario = Arrays.asList(
                "PRODUCTOS_VER",
                "CATEGORIAS_VER",
                "PEDIDOS_VER", "PEDIDOS_CREAR",
                "NOTIFICACIONES_VER",
                "MENU_DIA_VER",
                "CARRITO_GESTIONAR"
            );
            
            permisosUsuario.forEach(nombrePermiso -> {
                permisoRepository.findByNombre(nombrePermiso)
                    .ifPresent(usuario::agregarPermiso);
            });
            
            rolEntityRepository.save(usuario);
            log.info("Rol USUARIO creado con {} permisos", usuario.getPermisos().size());
        }
    }
}
