package com.example.Ejemplo.services.impl;

import com.example.Ejemplo.models.DetallePedido;
import com.example.Ejemplo.repository.DetallePedidoRepository;
import com.example.Ejemplo.services.DetallePedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DetallePedidoServiceImpl implements DetallePedidoService {

    private final DetallePedidoRepository detallePedidoRepository;

    @Override
    public int saveDetallePedido(DetallePedido detallePedido) {
        detallePedidoRepository.save(detallePedido);
        return 1;
    }

    @Override
    public List<DetallePedido> findAllDetallePedido() {
        return detallePedidoRepository.findAll();
    }
}
