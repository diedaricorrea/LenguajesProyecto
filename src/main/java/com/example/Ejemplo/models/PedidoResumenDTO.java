package com.example.Ejemplo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedidoResumenDTO {
    private int idUsuario;
    private String codigoPedido;
    private String nombreUsuario;
    private List<DetallePedido> productos;
    private LocalTime fechaEntrega;
    private boolean estado;
}
