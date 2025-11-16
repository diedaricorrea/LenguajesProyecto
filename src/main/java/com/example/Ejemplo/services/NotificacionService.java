package com.example.Ejemplo.services;

import com.example.Ejemplo.models.Notificacion;

import java.util.List;

public interface NotificacionService {
    void sendNotificacion(int idUsuario, String mensaje);
    
    void crearNotificacion(int idUsuario, String mensaje);
    
    List<Notificacion> findAllByUsuario_IdUsuario(int idUsuario);
    
    // Eliminar una notificación específica
    void deleteNotificacion(int idNotificacion, int idUsuario);
    
    // Eliminar todas las notificaciones de un usuario
    void deleteAllNotificaciones(int idUsuario);
    
    // Obtener las últimas N notificaciones
    List<Notificacion> getRecentNotificaciones(int idUsuario, int limit);
}
