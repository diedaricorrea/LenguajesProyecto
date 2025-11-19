package com.example.Ejemplo.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUsuario;

    @Column(name = "nombre")
    @NotBlank(message = "El nombre no puede estar vacio")
    @Size(min = 3, max = 50, message = "El nombre debe tener minimo 3 caracteres")
    private String nombre;
    
    @Column(name = "codigo_estudiantil", unique = true, length = 20)
    private String codigoEstudiantil; // C\u00f3digo estudiantil para estudiantes

    @Column(name = "correo", unique = true, nullable = false)
    private String correo;

    @Column(name = "password", nullable = false)
    @NotBlank(message = "Debes rellenar este campo")
    @Size(min = 8, message = "La contraseña debe ser de minimo 8 digitos")
    private String password;

    @Enumerated(EnumType.STRING)
    private Rol rol; // Mantener para compatibilidad
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol_entity")
    private RolEntity rolEntity; // Nuevo sistema de roles

    @Column(name = "fecha_ingreso", updatable = false)
    private LocalDateTime fechaIngreso;

    @Column(name = "estado")
    private boolean estado;

    @PrePersist
    protected void onCreate() {
        fechaIngreso = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "usuario")
    private List<Pedido> pedidos;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Venta> ventas;
    
    /**
     * Obtiene el rol efectivo (prioriza RolEntity sobre Rol enum)
     */
    public String getRolNombre() {
        if (rolEntity != null) {
            return rolEntity.getNombre();
        }
        return rol != null ? rol.name() : "USUARIO";
    }
    
    /**
     * Verifica si el usuario tiene un permiso específico
     */
    public boolean tienePermiso(String nombrePermiso) {
        if (rolEntity != null && rolEntity.getPermisos() != null) {
            return rolEntity.getPermisos().stream()
                .anyMatch(p -> p.getNombre().equals(nombrePermiso));
        }
        return false;
    }
}
