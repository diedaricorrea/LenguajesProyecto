package com.example.Ejemplo.services;

import com.example.Ejemplo.dto.*;
import com.example.Ejemplo.models.Rol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface UsuarioService {
    
    List<UsuarioDTO> findAllUsuarios();
    
    UsuarioResponseDTO findUsuarioById(Integer id);
    
    List<UsuarioDTO> buscarUsuarios(String texto);
    
    UsuarioResponseDTO crearUsuario(UsuarioCreateDTO usuarioDTO);
    
    UsuarioResponseDTO actualizarUsuario(UsuarioUpdateDTO usuarioDTO);
    
    void cambiarEstado(Integer idUsuario, boolean nuevoEstado);
    
    void eliminarUsuario(Integer idUsuario);
    
    @Deprecated
    List<UsuarioDTO> findAllUsuariosByNotRol(Rol rol);
    
    List<UsuarioDTO> findAllUsuariosAdministrativos();
    

    List<UsuarioDTO> findUsuariosByRol(Rol rol);
    

    UsuarioResponseDTO registrarUsuario(UsuarioRegistroDTO registroDTO);
    

    boolean existeCorreo(String correo);
    
    boolean existeCorreoPorOtroUsuario(String correo, Integer idUsuario);
    
    Page<UsuarioDTO> findUsuariosPaginados(Pageable pageable);
    
    EstadisticasUsuariosDTO obtenerEstadisticas();
    
    @Deprecated
    com.example.Ejemplo.models.Usuario findUsuarioEntityById(Integer id);
}
