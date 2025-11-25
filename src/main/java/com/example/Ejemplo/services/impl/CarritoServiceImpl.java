package com.example.Ejemplo.services.impl;

import com.example.Ejemplo.models.Carrito;
import com.example.Ejemplo.models.Producto;
import com.example.Ejemplo.repository.CarritoRepository;
import com.example.Ejemplo.repository.ProductoRepository;
import com.example.Ejemplo.services.CarritoService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarritoServiceImpl implements CarritoService {

    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;

    public CarritoServiceImpl(CarritoRepository carritoRepository, ProductoRepository productoRepository) {
        this.carritoRepository = carritoRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    public List<Producto> findAllProductos() {
        return productoRepository.findAll();
    }

    @Override
    public List<Producto> findAllProductosById(int id) {
        Optional<Producto> producto = productoRepository.findById(id);
        return producto.map(List::of).orElse(List.of());
    }


    @Override
    public List<Carrito> obtenerCarritosPorUsuario(int id) {
        return carritoRepository.findByIdUsuario_IdUsuario(id);
    }


    @Override
    public void saveCarrito(int id,int idProducto,int cantidad,double total) {
        carritoRepository.saveCarritoByIdUsuario(id, idProducto,cantidad,total);
    }

    @Override
    public int actualizarProductoAgregado(int idUsuario, int idProducto, int cantidad, double subTotal){
        return carritoRepository.updateCantidadCarrito(idUsuario,idProducto,cantidad,subTotal);
    }

    @Override
    public int eliminarProductoAgregado(int idUsuario,int idProducto){
        return carritoRepository.deleteCarritoByUsuarioIdAndProductoId(idUsuario,idProducto);
    }

    @Override
    public int limpiarCarrito(int idUsuario) {
        return carritoRepository.deleteCarritoByIdUsuario_IdUsuario(idUsuario);
    }
}
