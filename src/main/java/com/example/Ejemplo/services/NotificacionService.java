package com.example.Ejemplo.services;

import com.example.Ejemplo.models.Notificacion;

import java.util.List;

public interface NotificacionService {
    void sendNotificacion(int idUsuario, String mensaje);
    List<Notificacion> findAllByUsuario_IdUsuario(int idUsuario);
}
