package com.example.Ejemplo.services.impl;

import com.example.Ejemplo.models.Notificacion;
import com.example.Ejemplo.models.Usuario;
import com.example.Ejemplo.repository.NotificacionRepository;
import com.example.Ejemplo.repository.UsuarioRepository;
import com.example.Ejemplo.services.NotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public void sendNotificacion(int idUsuario, String mensaje) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setMensaje(mensaje);
        notificacion.setEstado(false); // No leída por defecto
        
        notificacionRepository.save(notificacion);
    }
    
    @Override
    @Transactional
    public void crearNotificacion(int idUsuario, String mensaje) {
        sendNotificacion(idUsuario, mensaje);
    }

    @Override
    public List<Notificacion> findAllByUsuario_IdUsuario(int idUsuario) {
        return notificacionRepository.findAllByUsuario_IdUsuario(idUsuario); 
    }
    
    @Override
    @Transactional
    public void deleteNotificacion(int idNotificacion, int idUsuario) {
        log.info("Intentando eliminar notificación ID: {} para usuario ID: {}", idNotificacion, idUsuario);
        // Verificar que la notificación pertenece al usuario antes de eliminar
        notificacionRepository.findById(idNotificacion)
                .filter(notif -> notif.getUsuario().getIdUsuario() == idUsuario)
                .ifPresentOrElse(
                    notif -> {
                        notificacionRepository.delete(notif);
                        log.info("Notificación eliminada exitosamente: ID {}", idNotificacion);
                    },
                    () -> log.warn("Notificación no encontrada o no pertenece al usuario: ID {}", idNotificacion)
                );
    }
    
    @Override
    @Transactional
    public void deleteAllNotificaciones(int idUsuario) {
        log.info("Eliminando todas las notificaciones para usuario ID: {}", idUsuario);
        List<Notificacion> notificaciones = notificacionRepository.findAllByUsuario_IdUsuario(idUsuario);
        log.info("Encontradas {} notificaciones para eliminar", notificaciones.size());
        notificacionRepository.deleteAll(notificaciones);
        log.info("Todas las notificaciones eliminadas exitosamente para usuario ID: {}", idUsuario);
    }
    
    @Override
    public List<Notificacion> getRecentNotificaciones(int idUsuario, int limit) {
        List<Notificacion> todasLasNotificaciones = notificacionRepository.findTopByUsuarioOrderByIdDesc(idUsuario);
        // Limitar a las últimas N notificaciones
        return todasLasNotificaciones.stream()
                .limit(limit)
                .toList();
    }
}
