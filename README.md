# UTPedidos - Sistema de Gestión de Cafetería

Sistema de gestión para cafeterías y kioskos universitarios desarrollado con Spring Boot.

## Características Principales

### Gestión de Productos
Sistema completo de administración de productos con interfaz Bootstrap moderna.

#### Funcionalidades
- **Listado de Productos**: Tabla responsive con filtros por búsqueda y estado
- **Crear Producto**: Formulario con validaciones y vista previa en tiempo real
- **Editar Producto**: Modificación de datos existentes con imagen actual
- **Actualizar Stock**: Modal rápido para agregar inventario
- **Activar/Desactivar**: Control de disponibilidad de productos
- **Gestión de Imágenes**: Subida de imágenes con preview
- **Categorización**: Asociación de productos a categorías

#### Endpoints
```
GET    /admin/productos                        - Listar productos
GET    /admin/productos/nuevo                  - Formulario nuevo producto
GET    /admin/productos/{id}/editar            - Formulario editar producto
POST   /admin/productos/guardar                - Guardar/actualizar producto
POST   /admin/productos/{id}/actualizar-stock  - Actualizar stock
POST   /admin/productos/{id}/cambiar-estado    - Cambiar estado (AJAX)
POST   /admin/productos/{id}/activar           - Activar producto
POST   /admin/productos/{id}/desactivar        - Desactivar producto
POST   /admin/productos/{id}/eliminar-permanente - Eliminar definitivamente
```

#### Vistas
- `productosLista.html`: Lista principal con tabla Bootstrap
- `productoFormulario.html`: Formulario de creación/edición
- `accionesProducto.html`: Vista de actualización rápida de stock

---

### Gestión de Categorías
Sistema para organizar productos en categorías lógicas.

#### Funcionalidades
- **Listado de Categorías**: Tabla con contador de productos asociados
- **Crear Categoría**: Formulario simple con validaciones
- **Editar Categoría**: Modificación del nombre
- **Eliminar Categoría**: Solo si no tiene productos asociados
- **Búsqueda**: Filtro por nombre de categoría
- **Estadísticas**: Total de categorías y productos
- **Protección**: No permite eliminar categorías con productos

#### Endpoints
```
GET    /admin/categorias              - Listar categorías
GET    /admin/categorias/nueva        - Formulario nueva categoría
GET    /admin/categorias/{id}/editar  - Formulario editar categoría
POST   /admin/categorias/guardar      - Guardar/actualizar categoría
POST   /admin/categorias/{id}/eliminar - Eliminar categoría
GET    /admin/categorias/api/listar   - API REST listar
GET    /admin/categorias/api/existe   - API REST verificar existencia
```

#### Vistas
- `categoriasLista.html`: Lista principal con estadísticas
- `categoriaFormulario.html`: Formulario de creación/edición

---

## Modelo de Datos

### Relación Producto-Categoría

**Tipo de Relación:** Muchos a Uno (N:1)
- Cada producto pertenece a UNA categoría
- Cada categoría puede tener MUCHOS productos

```
Categoría 1 ────────┐
                     ├──→ Producto 1
                     ├──→ Producto 2
                     └──→ Producto 3

Categoría 2 ────────┐
                     ├──→ Producto 4
                     └──→ Producto 5
```

**Ejemplo Real (Cafetería Universitaria):**
```
Bebidas Calientes
├── Café Americano
├── Café Latte
└── Té Verde

Bebidas Frías
├── Jugo de Naranja
├── Limonada
└── Gaseosa

Comidas
├── Sandwich Mixto
├── Hamburguesa
└── Ensalada César

Postres
├── Brownie
├── Cheesecake
└── Galletas
```

### Entidades

#### Producto
```java
@Entity
@Table(name = "productos")
class Producto {
    Integer idProducto;
    String nombre;
    Double precio;
    String descripcion;
    Integer stock;
    Boolean estado;
    String imagenUrl;
    Categoria categoria; // ManyToOne
}
```

#### Categoría
```java
@Entity
@Table(name = "categorias")
class Categoria {
    Integer idCategoria;
    String nombre;
    List<Producto> productos; // OneToMany
}
```

---

## Arquitectura

### Patrón de Diseño: DTO (Data Transfer Object)

El sistema utiliza DTOs para separar la capa de presentación de la capa de persistencia:

```
Controller → DTO → Service → Entity → Repository
           ↓                     ↑
         View              Database
```

#### Ventajas
1. **Seguridad**: No expone entidades directamente
2. **Flexibilidad**: Control sobre qué datos se envían/reciben
3. **Validación**: Validaciones específicas por operación
4. **Performance**: Evita lazy loading issues

### Capas

```
┌─────────────────────────────────────┐
│         Controllers                 │
│  - ProductoAdminController          │
│  - CategoriaAdminController         │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│         DTOs                        │
│  - ProductoDTO                      │
│  - ProductoCreateDTO                │
│  - ProductoResponseDTO              │
│  - CategoriaDTO                     │
│  - CategoriaCreateDTO               │
│  - CategoriaResponseDTO             │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│         Services                    │
│  - ProductoService                  │
│  - CategoriaService                 │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│         Repositories                │
│  - ProductoRepository               │
│  - CategoriaRepository              │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│         Database (MySQL)            │
└─────────────────────────────────────┘
```

---

## Tecnologías Utilizadas

- **Backend**: Spring Boot 3.x
- **ORM**: Spring Data JPA / Hibernate
- **Base de Datos**: MySQL
- **Frontend**: Thymeleaf + Bootstrap 5
- **Seguridad**: Spring Security
- **Validación**: Bean Validation (Jakarta)
- **Logging**: SLF4J + Logback
- **Build**: Maven

---

## Instalación y Configuración

### Requisitos Previos
- JDK 17 o superior
- Maven 3.6+
- MySQL 8.0+

### Configuración de Base de Datos

Editar `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/utpedidos
spring.datasource.username=root
spring.datasource.password=tu_password
spring.jpa.hibernate.ddl-auto=update
```

### Ejecutar la Aplicación

```bash
# Con Maven Wrapper
./mvnw spring-boot:run

# Con Maven instalado
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

---

## Sistema de Roles y Permisos

### Roles Disponibles
- **ADMINISTRADOR**: Acceso completo al sistema
- **TRABAJADOR**: Gestión de productos, categorías y pedidos
- **USUARIO**: Acceso al catálogo y carrito de compras

### Permisos por Módulo

#### Productos
- `PRODUCTOS_VER`: Ver lista de productos
- `PRODUCTOS_CREAR`: Crear nuevos productos
- `PRODUCTOS_EDITAR`: Modificar productos existentes
- `PRODUCTOS_ELIMINAR`: Desactivar productos
- `PRODUCTOS_GESTIONAR`: Permiso completo

#### Categorías
- `CATEGORIAS_VER`: Ver lista de categorías
- `CATEGORIAS_CREAR`: Crear nuevas categorías
- `CATEGORIAS_EDITAR`: Modificar categorías
- `CATEGORIAS_ELIMINAR`: Eliminar categorías
- `CATEGORIAS_GESTIONAR`: Permiso completo

---

## Características de Diseño

### Sin CSS Personalizado
- Todo el diseño utiliza Bootstrap 5 nativo
- No hay clases CSS custom de IA
- Fácil mantenimiento y actualización

### Sin Emoticones
- Interfaz profesional
- Solo iconos de Bootstrap Icons
- Texto claro y descriptivo

### Responsive
- Diseño adaptable a móviles, tablets y desktop
- Tablas responsive con scroll horizontal
- Formularios optimizados para dispositivos táctiles

### Accesibilidad
- Labels correctos en formularios
- Mensajes de error claros
- Confirmaciones antes de acciones destructivas
- Feedback visual de acciones

---

## Buenas Prácticas Implementadas

### Backend
1. **DTOs**: Separación entre capa de presentación y persistencia
2. **Logging**: Registro de operaciones importantes
3. **Validaciones**: En DTOs y lógica de negocio
4. **Excepciones**: Manejo centralizado de errores
5. **Transacciones**: Gestión automática con `@Transactional`
6. **Seguridad**: Autenticación y autorización en endpoints

### Frontend
7. **Bootstrap 5**: Framework CSS moderno
8. **Thymeleaf**: Motor de plantillas server-side
9. **Validaciones**: HTML5 + JavaScript
10. **UX**: Mensajes claros, confirmaciones, feedback
11. **Responsive**: Diseño adaptable
12. **Fragmentos**: Reutilización de componentes

### Base de Datos
13. **Índices**: En columnas frecuentemente consultadas
14. **Foreign Keys**: Integridad referencial
15. **Nombres descriptivos**: Convención clara
16. **Lazy Loading**: Para optimizar consultas

---

## Autor

Sistema desarrollado para el proyecto de Lenguajes de Programación

---

## Licencia

Este proyecto es de uso académico
