package com.example.Ejemplo.repository;


import java.util.List;

import com.example.Ejemplo.models.Carrito;
import com.example.Ejemplo.models.CarritoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, CarritoId> {
    @Modifying
    @Transactional
    @Query(value = "UPDATE carrito SET cantidad = :cantidad, total = :subTotal WHERE id_producto = :idProducto AND id_usuario = :idUsuario", nativeQuery = true)
    int updateCantidadCarrito(@Param("idUsuario") int idUsuario,
                              @Param("idProducto") int idProducto,
                              @Param("cantidad") int cantidad,
                              @Param("subTotal") double subTotal);

    @Modifying
    @Transactional
    @Query("DELETE FROM Carrito c WHERE c.idUsuario.idUsuario = :usuarioId AND c.idProducto.idProducto = :productoId")
    int deleteCarritoByUsuarioIdAndProductoId(@Param("usuarioId") int usuarioId, @Param("productoId") int productoId);

    List<Carrito> findByIdUsuario_IdUsuario(Integer idUsuario);

    @Transactional
    int deleteCarritoByIdUsuario_IdUsuario(Integer idUsuario);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO carrito (id_usuario, id_producto, cantidad, total) VALUES (:idUsuario, :idProducto, :cantidad, :total)", nativeQuery = true)
    void saveCarritoByIdUsuario(@Param("idUsuario") int idUsuario,
                                @Param("idProducto") int idProducto,
                                @Param("cantidad") int cantidad,
                                @Param("total") double total);
}