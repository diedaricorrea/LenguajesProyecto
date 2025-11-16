package com.example.Ejemplo.repository;

import com.example.Ejemplo.models.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {
    List<Notificacion> findAllByUsuario_IdUsuario(int usuarioIdUsuario);
}
