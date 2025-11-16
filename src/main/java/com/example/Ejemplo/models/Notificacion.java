package com.example.Ejemplo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notificaciones")
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idNotificacion;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    private String mensaje;

    private boolean estado;

}
