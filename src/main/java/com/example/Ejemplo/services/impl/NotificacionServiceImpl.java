package com.example.Ejemplo.services.impl;

import com.example.Ejemplo.models.Notificacion;
import com.example.Ejemplo.repository.NotificacionRepository;
import com.example.Ejemplo.services.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository notificacionRepository;

    @Override
    public void sendNotificacion(int idUsuario, String mensaje) {
        notificacionRepository.sendNotificacion(idUsuario, mensaje);
    }

    @Override
    public List<Notificacion> findAllByUsuario_IdUsuario(int idUsuario) {
        return notificacionRepository.findAllByUsuario_IdUsuario(idUsuario); 
    }
}
