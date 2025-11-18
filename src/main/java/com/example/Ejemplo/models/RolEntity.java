package com.example.Ejemplo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class RolEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRol;
    
    @Column(unique = true, nullable = false, length = 50)
    private String nombre; // Ej: "ADMINISTRADOR", "TRABAJADOR", "USUARIO"
    
    @Column(length = 255)
    private String descripcion;
    
    @Column(nullable = false)
    private boolean activo = true;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "rol_permiso",
        joinColumns = @JoinColumn(name = "id_rol"),
        inverseJoinColumns = @JoinColumn(name = "id_permiso")
    )
    private Set<Permiso> permisos = new HashSet<>();
    
    @OneToMany(mappedBy = "rolEntity")
    private Set<Usuario> usuarios = new HashSet<>();
    
    public RolEntity(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = true;
    }
    
    public void agregarPermiso(Permiso permiso) {
        this.permisos.add(permiso);
        permiso.getRoles().add(this);
    }
    
    public void removerPermiso(Permiso permiso) {
        this.permisos.remove(permiso);
        permiso.getRoles().remove(this);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RolEntity)) return false;
        RolEntity rolEntity = (RolEntity) o;
        return Objects.equals(idRol, rolEntity.idRol) && 
               Objects.equals(nombre, rolEntity.nombre);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idRol, nombre);
    }
    
    @Override
    public String toString() {
        return "RolEntity{" +
                "idRol=" + idRol +
                ", nombre='" + nombre + '\'' +
                ", activo=" + activo +
                '}';
    }
}
