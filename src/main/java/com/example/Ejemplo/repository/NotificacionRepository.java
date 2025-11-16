package com.example.Ejemplo.repository;

import com.example.Ejemplo.models.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {
    List<Notificacion> findAllByUsuario_IdUsuario(int usuarioIdUsuario);
    
    // Obtener las últimas N notificaciones ordenadas por ID descendente
    @Query("SELECT n FROM Notificacion n WHERE n.usuario.idUsuario = :idUsuario ORDER BY n.idNotificacion DESC")
    List<Notificacion> findTopByUsuarioOrderByIdDesc(@Param("idUsuario") int idUsuario);
}
