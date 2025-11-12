package com.example.Ejemplo.services.impl;

import com.example.Ejemplo.models.Rol;
import com.example.Ejemplo.models.Usuario;
import com.example.Ejemplo.repository.UsuarioRepository;
import com.example.Ejemplo.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public List<Usuario> findAllUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> findUsuarioById(Integer id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Optional<Usuario> findUsuarioPorNombre(String nombre) {
        // Método no implementado
        return Optional.empty();
    }

    @Override
    public Usuario saveUser(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public void updateUsuario(Integer id) {
        // Método no implementado
    }

    @Override
    public List<Usuario> findAllUsuariosByNotRol(Rol rol) {
        return usuarioRepository.findByRolNot(rol);
    }



    @Override
    public Optional<Usuario> findUsuarioByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    public List<Integer> findAllIdUsuario(int idUsuario) {
        return usuarioRepository.findAllIdPedidosByUsuario(idUsuario);
    };

}
