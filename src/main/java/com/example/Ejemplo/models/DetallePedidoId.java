package com.example.Ejemplo.models;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DetallePedidoId implements Serializable {
    private Integer idPedido;
    private Integer idProducto;

    public DetallePedidoId() {}

    public DetallePedidoId(Integer idPedido, Integer idProducto) {
        this.idPedido = idPedido;
        this.idProducto = idProducto;
    }

    public Integer getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Integer idPedido) {
        this.idPedido = idPedido;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    // equals() y hashCode() son obligatorios
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DetallePedidoId)) return false;
        DetallePedidoId that = (DetallePedidoId) o;
        return Objects.equals(idPedido, that.idPedido) &&
                Objects.equals(idProducto, that.idProducto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPedido, idProducto);
    }
}
