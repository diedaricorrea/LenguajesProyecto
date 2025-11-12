package com.example.Ejemplo.repository;

import com.example.Ejemplo.models.DetallePedido;
import com.example.Ejemplo.models.DetallePedidoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, DetallePedidoId> {
    List<DetallePedido> findAll();
}
