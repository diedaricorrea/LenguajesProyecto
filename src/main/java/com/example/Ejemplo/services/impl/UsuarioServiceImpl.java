package com.example.Ejemplo.services.impl;

import com.example.Ejemplo.dto.*;
import com.example.Ejemplo.mapper.UsuarioMapper;
import com.example.Ejemplo.models.Rol;
import com.example.Ejemplo.models.Usuario;
import com.example.Ejemplo.repository.UsuarioRepository;
import com.example.Ejemplo.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de usuarios
 * Usa DTOs y aplica validaciones de negocio
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioServiceImpl implements UsuarioService {
    
    private static final Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);
    
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UsuarioDTO> findAllUsuarios() {
        logger.info("Obteniendo todos los usuarios");
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioResponseDTO findUsuarioById(Integer id) {
        logger.info("Buscando usuario con ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
        
        return usuarioMapper.toResponseDTO(usuario);
    }

    @Override
    public List<UsuarioDTO> buscarUsuarios(String texto) {
        logger.info("Buscando usuarios con texto: {}", texto);
        
        if (texto == null || texto.trim().isEmpty()) {
            return findAllUsuarios();
        }
        
        return usuarioRepository.buscarPorNombreOCorreo(texto).stream()
                .map(usuarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UsuarioResponseDTO crearUsuario(UsuarioCreateDTO usuarioDTO) {
        logger.info("Creando nuevo usuario: {}", usuarioDTO.getCorreo());
        
        // Validar que el correo no exista
        if (usuarioRepository.existsByCorreo(usuarioDTO.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un usuario con el correo: " + usuarioDTO.getCorreo());
        }
        
        // Convertir DTO a entidad
        Usuario usuario = usuarioMapper.toEntity(usuarioDTO);
        
        // Encriptar contraseña
        usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        
        // Guardar
        Usuario guardado = usuarioRepository.save(usuario);
        
        logger.info("Usuario creado exitosamente con ID: {}", guardado.getIdUsuario());
        return usuarioMapper.toResponseDTO(guardado);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizarUsuario(UsuarioUpdateDTO usuarioDTO) {
        logger.info("Actualizando usuario ID: {}", usuarioDTO.getIdUsuario());
        
        Usuario usuario = usuarioRepository.findById(usuarioDTO.getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioDTO.getIdUsuario()));
        
        // Actualizar campos
        usuarioMapper.updateEntity(usuario, usuarioDTO);
        
        // Guardar cambios
        Usuario actualizado = usuarioRepository.save(usuario);
        
        logger.info("Usuario actualizado exitosamente: {}", actualizado.getIdUsuario());
        return usuarioMapper.toResponseDTO(actualizado);
    }

    @Override
    @Transactional
    public void cambiarEstado(Integer idUsuario, boolean nuevoEstado) {
        logger.info("Cambiando estado del usuario ID {} a {}", idUsuario, nuevoEstado);
        
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + idUsuario));
        
        usuario.setEstado(nuevoEstado);
        usuarioRepository.save(usuario);
        
        logger.info("Estado cambiado exitosamente");
    }

    @Override
    @Transactional
    public void eliminarUsuario(Integer idUsuario) {
        logger.info("Desactivando usuario ID: {}", idUsuario);
        cambiarEstado(idUsuario, false);
    }

    @Override
    @Deprecated
    public List<UsuarioDTO> findAllUsuariosByNotRol(Rol rol) {
        logger.info("Obteniendo usuarios excluyendo rol: {}", rol);
        return usuarioRepository.findByRolNot(rol).stream()
                .map(usuarioMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<UsuarioDTO> findAllUsuariosAdministrativos() {
        logger.info("Obteniendo todos los usuarios administrativos/trabajadores");
        return usuarioRepository.findAllUsuariosAdministrativos().stream()
                .map(usuarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UsuarioDTO> findUsuariosByRol(Rol rol) {
        logger.info("Obteniendo usuarios con rol: {}", rol);
        return usuarioRepository.findByRol(rol).stream()
                .map(usuarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UsuarioResponseDTO registrarUsuario(UsuarioRegistroDTO registroDTO) {
        logger.info("Registrando nuevo estudiante: {} ({})", registroDTO.getNombre(), registroDTO.getCodigoEstudiantil());
        
        // Validar que el código estudiantil no exista
        if (usuarioRepository.existsByCodigoEstudiantil(registroDTO.getCodigoEstudiantil())) {
            throw new IllegalArgumentException("Ya existe una cuenta con ese código estudiantil");
        }
        
        // El correo se genera automáticamente: codigoEstudiantil@utp.edu.pe
        // No necesita validación adicional porque el código ya es único
        
        // Validar que las contraseñas coincidan
        if (!registroDTO.getPassword().equals(registroDTO.getConfirmarPassword())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }
        
        // Convertir DTO a entidad
        Usuario usuario = usuarioMapper.toEntityFromRegistro(registroDTO);
        
        // Encriptar contraseña
        usuario.setPassword(passwordEncoder.encode(registroDTO.getPassword()));
        
        // Guardar
        Usuario guardado = usuarioRepository.save(usuario);
        
        logger.info("Estudiante registrado exitosamente - ID: {}, Código: {}", 
                   guardado.getIdUsuario(), guardado.getCodigoEstudiantil());
        return usuarioMapper.toResponseDTO(guardado);
    }

    @Override
    public boolean existeCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    @Override
    public boolean existeCorreoPorOtroUsuario(String correo, Integer idUsuario) {
        return usuarioRepository.existsByCorreoAndIdUsuarioNot(correo, idUsuario);
    }

    @Override
    public Page<UsuarioDTO> findUsuariosPaginados(Pageable pageable) {
        logger.info("Obteniendo usuarios paginados: página {}, tamaño {}", 
                    pageable.getPageNumber(), pageable.getPageSize());
        
        return usuarioRepository.findAll(pageable)
                .map(usuarioMapper::toDTO);
    }

    @Override
    public EstadisticasUsuariosDTO obtenerEstadisticas() {
        logger.info("Obteniendo estadísticas de usuarios");
        
        EstadisticasUsuariosDTO stats = new EstadisticasUsuariosDTO();
        stats.setTotalUsuarios(usuarioRepository.count());
        stats.setUsuariosActivos(usuarioRepository.countByEstado(true));
        stats.setAdministradores(usuarioRepository.countByRol(Rol.ADMINISTRADOR));
        stats.setTrabajadores(usuarioRepository.countByRol(Rol.TRABAJADOR));
        stats.setClientes(usuarioRepository.countByRol(Rol.USUARIO));
        
        return stats;
    }
    
    // ============================================================
    // Métodos legacy para compatibilidad (deprecados)
    // ============================================================
    
    /**
     * @deprecated Usar findUsuarioById que retorna DTO
     */
    @Deprecated
    public Usuario findUsuarioEntityById(Integer id) {
        return usuarioRepository.findById(id).orElse(null);
    }
    
    /**
     * @deprecated Usar crearUsuario o actualizarUsuario
     */
    @Deprecated
    @Transactional
    public Usuario saveUser(Usuario usuario) {
        logger.warn("Usando método deprecated saveUser. Migrar a crearUsuario o actualizarUsuario");
        return usuarioRepository.save(usuario);
    }
    
    /**
     * @deprecated Usar buscarUsuarios
     */
    @Deprecated
    public Usuario findUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo).orElse(null);
    }
    
    public List<Integer> findAllIdUsuario(int idUsuario) {
        return usuarioRepository.findAllIdPedidosByUsuario(idUsuario);
    }
}
