package com.example.Ejemplo.repository;

import com.example.Ejemplo.models.EstadoPedido;
import com.example.Ejemplo.models.Pedido;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidosRepository extends JpaRepository<Pedido, Integer> {
    
    /**
     * Busca pedidos por estado
     */
    List<Pedido> findAllByEstado(EstadoPedido estado);

    /**
     * Marca un pedido como entregado (soft delete)
     */
    @Modifying
    @Transactional
    @Query("UPDATE Pedido p SET p.estado = 'ENTREGADO' WHERE p.codigoPedido = :idPedido")
    int deleteByCodigoPedido(@Param("idPedido") String codigoPedido);

    /**
     * Busca pedidos por código
     */
    @Query("SELECT p FROM Pedido p WHERE p.codigoPedido = :codigo")
    List<Pedido> findByCodigoPedido(@Param("codigo") String codigo);

    /**
     * Obtiene todos los pedidos de un usuario con sus detalles
     */
    @Query("SELECT DISTINCT p FROM Pedido p JOIN FETCH p.detallePedido dp JOIN FETCH dp.producto WHERE p.usuario.idUsuario = :idUsuario")
    List<Pedido> findAllByUsuario_IdUsuario(@Param("idUsuario") int idUsuario);
    
    /**
     * Busca pedidos por rango de fechas
     */
    @Query("SELECT p FROM Pedido p WHERE p.fechaPedido BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaPedido DESC")
    List<Pedido> findByFechaPedidoBetween(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);
}
