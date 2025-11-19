package com.example.Ejemplo.services;

import com.example.Ejemplo.dto.*;
import com.example.Ejemplo.models.Rol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Servicio para gestionar usuarios del sistema
 * Sigue el patrón de usar DTOs en lugar de entidades
 */
public interface UsuarioService {
    
    /**
     * Obtiene todos los usuarios como DTOs
     */
    List<UsuarioDTO> findAllUsuarios();
    
    /**
     * Obtiene un usuario por ID
     */
    UsuarioResponseDTO findUsuarioById(Integer id);
    
    /**
     * Busca usuarios por nombre o correo
     */
    List<UsuarioDTO> buscarUsuarios(String texto);
    
    /**
     * Crea un nuevo usuario (para administradores)
     */
    UsuarioResponseDTO crearUsuario(UsuarioCreateDTO usuarioDTO);
    
    /**
     * Actualiza un usuario existente
     */
    UsuarioResponseDTO actualizarUsuario(UsuarioUpdateDTO usuarioDTO);
    
    /**
     * Cambia el estado de un usuario (activar/desactivar)
     */
    void cambiarEstado(Integer idUsuario, boolean nuevoEstado);
    
    /**
     * Elimina (desactiva) un usuario
     */
    void eliminarUsuario(Integer idUsuario);
    
    /**
     * Busca usuarios excluyendo un rol específico
     * @deprecated Usar findAllUsuariosAdministrativos() para el panel admin
     */
    @Deprecated
    List<UsuarioDTO> findAllUsuariosByNotRol(Rol rol);
    
    /**
     * Obtiene todos los usuarios administrativos/trabajadores
     * Excluye solo usuarios con RolEntity "USUARIO"
     */
    List<UsuarioDTO> findAllUsuariosAdministrativos();
    
    /**
     * Obtiene usuarios por rol
     */
    List<UsuarioDTO> findUsuariosByRol(Rol rol);
    
    /**
     * Registra un nuevo usuario (registro público)
     */
    UsuarioResponseDTO registrarUsuario(UsuarioRegistroDTO registroDTO);
    
    /**
     * Valida si un correo ya está en uso
     */
    boolean existeCorreo(String correo);
    
    /**
     * Valida si un correo está en uso por otro usuario (útil para actualización)
     */
    boolean existeCorreoPorOtroUsuario(String correo, Integer idUsuario);
    
    /**
     * Obtiene usuarios con paginación
     */
    Page<UsuarioDTO> findUsuariosPaginados(Pageable pageable);
    
    /**
     * Obtiene estadísticas de usuarios
     */
    EstadisticasUsuariosDTO obtenerEstadisticas();
    
    // ============================================================
    // Métodos legacy para compatibilidad (deprecados)
    // ============================================================
    
    /**
     * @deprecated Usar findUsuarioById que retorna DTO
     * Obtiene la entidad Usuario por ID (para compatibilidad)
     */
    @Deprecated
    com.example.Ejemplo.models.Usuario findUsuarioEntityById(Integer id);
}
