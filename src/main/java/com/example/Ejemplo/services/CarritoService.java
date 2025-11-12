package com.example.Ejemplo.services;

import com.example.Ejemplo.models.Carrito;
import com.example.Ejemplo.models.Producto;

import java.util.List;


public interface CarritoService {
    List<Producto> findAllProductos();
    List<Producto> findAllProductosById(int id);
    List<Carrito> obtenerCarritosPorUsuario(int id);
    void saveCarrito(int id,int idProducto,int cantidad,double total);
    int actualizarProductoAgregado(int idUsuario, int idProducto, int cantidad,double subTotal);
    int eliminarProductoAgregado(int idUsuario,int idProducto);

    int limpiarCarrito(int idUsuario);
}