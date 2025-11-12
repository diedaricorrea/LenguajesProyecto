package com.example.Ejemplo.repository;

import com.example.Ejemplo.models.Notificacion;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO notificaciones(id_usuario,mensaje) VALUES(:idUsuario,:message)", nativeQuery = true)
    void sendNotificacion(@Param("idUsuario") int idUsuario,@Param("message") String message);

    List<Notificacion> findAllByUsuario_IdUsuario(int usuarioIdUsuario);
}
