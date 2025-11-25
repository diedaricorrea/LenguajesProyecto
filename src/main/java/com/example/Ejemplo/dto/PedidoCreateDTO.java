package com.example.Ejemplo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoCreateDTO {
    private Integer idUsuario;
    private LocalTime horaEntrega;
    
    // Opcional: si se permite elegir método de pago
    private String metodoPago;
    private String notasEspeciales;
}
