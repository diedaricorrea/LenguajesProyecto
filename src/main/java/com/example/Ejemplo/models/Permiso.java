package com.example.Ejemplo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entidad que representa los permisos del sistema
 * Un permiso es una acción específica que puede realizarse
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "permisos")
public class Permiso {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPermiso;
    
    @Column(unique = true, nullable = false, length = 100)
    private String nombre; // Ej: "PRODUCTOS_VER", "PRODUCTOS_CREAR", "PRODUCTOS_EDITAR"
    
    @Column(length = 255)
    private String descripcion;
    
    @Column(length = 50)
    private String modulo; // Ej: "PRODUCTOS", "USUARIOS", "PEDIDOS", "CATEGORIAS"
    
    @ManyToMany(mappedBy = "permisos")
    private Set<RolEntity> roles = new HashSet<>();
    
    public Permiso(String nombre, String descripcion, String modulo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.modulo = modulo;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permiso)) return false;
        Permiso permiso = (Permiso) o;
        return Objects.equals(idPermiso, permiso.idPermiso) && 
               Objects.equals(nombre, permiso.nombre);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idPermiso, nombre);
    }
    
    @Override
    public String toString() {
        return "Permiso{" +
                "idPermiso=" + idPermiso +
                ", nombre='" + nombre + '\'' +
                ", modulo='" + modulo + '\'' +
                '}';
    }
}
