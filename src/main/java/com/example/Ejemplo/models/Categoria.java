package com.example.Ejemplo.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Categoria - Representa las categorías de productos del sistema
 * Relación: Una categoría puede tener muchos productos (1:N)
 */
@Entity
@Table(name = "categorias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer idCategoria;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    /**
     * Relación bidireccional con Producto
     * NOTA: Para evitar lazy loading issues, esta lista NO se expone directamente en DTOs
     * Se usa fetch = LAZY para optimizar consultas
     */
    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<Producto> productos = new ArrayList<>();
    
    // Constructor de conveniencia
    public Categoria(String nombre) {
        this.nombre = nombre;
        this.productos = new ArrayList<>();
    }
    
    /**
     * Método helper para obtener la cantidad de productos sin cargar toda la lista
     * SEGURO: Verifica que la colección esté inicializada antes de acceder
     */
    public int getCantidadProductos() {
        try {
            if (productos != null && org.hibernate.Hibernate.isInitialized(productos)) {
                return productos.size();
            }
        } catch (Exception e) {
            // Si hay error, retornar 0
        }
        return 0;
    }
}