package com.example.Ejemplo.services.impl;

import com.example.Ejemplo.models.Notificacion;
import com.example.Ejemplo.models.Usuario;
import com.example.Ejemplo.repository.NotificacionRepository;
import com.example.Ejemplo.repository.UsuarioRepository;
import com.example.Ejemplo.services.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
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
}
