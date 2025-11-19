package com.example.Ejemplo.repository;

import com.example.Ejemplo.models.Rol;
import com.example.Ejemplo.models.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    /**
     * Busca usuarios que NO tengan el rol especificado
     */
    List<Usuario> findByRolNot(Rol rol);
    
    /**
     * Busca usuario por correo electrónico
     */
    Optional<Usuario> findByCorreo(String correo);
    
    /**
     * Busca usuarios por nombre (búsqueda parcial, case insensitive)
     */
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);
    
    /**
     * Busca usuarios por rol específico
     */
    List<Usuario> findByRol(Rol rol);
    
    /**
     * Busca usuarios por estado (activo/inactivo)
     */
    List<Usuario> findByEstado(boolean estado);
    
    /**
     * Busca usuarios activos con rol específico
     */
    List<Usuario> findByRolAndEstado(Rol rol, boolean estado);
    
    /**
     * Busca usuarios con paginación
     */
    Page<Usuario> findAll(Pageable pageable);
    
    /**
     * Busca usuarios por rol con paginación
     */
    Page<Usuario> findByRol(Rol rol, Pageable pageable);
    
    /**
     * Busca usuarios excluyendo un rol con paginación
     */
    Page<Usuario> findByRolNot(Rol rol, Pageable pageable);
    
    /**
     * Busca por nombre o correo (búsqueda parcial)
     */
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) " +
           "OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<Usuario> buscarPorNombreOCorreo(@Param("texto") String texto);
    
    /**
     * Cuenta usuarios por rol
     */
    long countByRol(Rol rol);
    
    /**
     * Cuenta usuarios activos
     */
    long countByEstado(boolean estado);
    
    /**
     * Obtiene todos los IDs de pedidos de un usuario
     */
    @Query("SELECT p.idPedido FROM Pedido p WHERE p.usuario.idUsuario = :idUsuario")
    List<Integer> findAllIdPedidosByUsuario(@Param("idUsuario") int idUsuario);
    
    /**
     * Verifica si existe un correo (útil para validación)
     */
    boolean existsByCorreo(String correo);
    
    /**
     * Verifica si existe un correo excluyendo un ID específico (útil para actualización)
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usuario u " +
           "WHERE u.correo = :correo AND u.idUsuario <> :idUsuario")
    boolean existsByCorreoAndIdUsuarioNot(@Param("correo") String correo, @Param("idUsuario") Integer idUsuario);
    
    /**
     * Busca usuarios excluyendo aquellos cuyo RolEntity tenga un nombre específico
     * Útil para panel admin: excluir usuarios normales del catálogo
     */
    @Query("SELECT u FROM Usuario u WHERE u.rolEntity.nombre != :nombreRol OR u.rolEntity IS NULL")
    List<Usuario> findByRolEntityNombreNot(@Param("nombreRol") String nombreRol);
    
    /**
     * Busca todos los usuarios trabajadores/administrativos
     * Excluye solo usuarios con rol "USUARIO" en RolEntity
     */
    @Query("SELECT u FROM Usuario u WHERE u.rolEntity IS NULL OR u.rolEntity.nombre != 'USUARIO'")
    List<Usuario> findAllUsuariosAdministrativos();
}
