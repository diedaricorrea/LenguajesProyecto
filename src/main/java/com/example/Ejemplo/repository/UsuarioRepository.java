package com.example.Ejemplo.repository;

import com.example.Ejemplo.models.Rol;
import com.example.Ejemplo.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    List<Usuario> findByRolNot(Rol rol);

    @Query("SELECT p.idPedido FROM Pedido p WHERE p.usuario.idUsuario = :idUsuario")
    List<Integer> findAllIdPedidosByUsuario(@Param("idUsuario") int idUsuario);

    Optional<Usuario> findByCorreo(String correo);
}
