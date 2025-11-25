package com.example.Ejemplo.services;

import com.example.Ejemplo.models.Notificacion;

import java.util.List;

public interface NotificacionService {
    void sendNotificacion(int idUsuario, String mensaje);
    
    void crearNotificacion(int idUsuario, String mensaje);
    
    List<Notificacion> findAllByUsuario_IdUsuario(int idUsuario);
    
    void deleteNotificacion(int idNotificacion, int idUsuario);
    
    void deleteAllNotificaciones(int idUsuario);

    List<Notificacion> getRecentNotificaciones(int idUsuario, int limit);
}
