# Sistema de Gestión de Categorías - UTPedidos

## Descripción General

Se ha implementado un **sistema completo de gestión de categorías** para el proyecto UTPedidos. Este sistema permite administrar las categorías de productos de forma independiente y facilita la creación de nuevos productos.

## Componentes Implementados

### 1. **Backend - Capa de Servicio**

#### CategoriaService (Interface)
Ubicación: `src/main/java/com/example/Ejemplo/services/CategoriaService.java`

Métodos disponibles:
- `findAll()` - Obtiene todas las categorías
- `findById(Integer id)` - Busca una categoría por ID
- `findByNombre(String nombre)` - Busca una categoría por nombre
- `save(Categoria categoria)` - Guarda o actualiza una categoría
- `deleteById(Integer id)` - Elimina una categoría
- `existsByNombre(String nombre)` - Verifica si existe una categoría
- `countProductosByCategoria(Integer idCategoria)` - Cuenta productos asociados

#### CategoriaServiceImpl (Implementación)
Ubicación: `src/main/java/com/example/Ejemplo/services/CategoriaServiceImpl.java`

Características:
- **Validaciones**: No permite nombres vacíos o duplicados
- **Protección**: No permite eliminar categorías con productos asociados
- **Transaccional**: Operaciones seguras con `@Transactional`

### 2. **Backend - Controlador**

#### CategoriaController
Ubicación: `src/main/java/com/example/Ejemplo/controllers/CategoriaController.java`

Endpoints disponibles:
- `GET /categorias` - Muestra la página de gestión
- `POST /categorias/guardar` - Guarda o actualiza una categoría
- `GET /categorias/editar/{id}` - Carga una categoría para editar
- `GET /categorias/eliminar/{id}` - Elimina una categoría
- `GET /categorias/api/listar` - API REST para obtener categorías (AJAX)
- `GET /categorias/api/existe` - API REST para verificar existencia

### 3. **Frontend - Vista de Administración**

#### categorias.html
Ubicación: `src/main/resources/templates/administrador/categorias.html`

Características:
- **Diseño responsivo** con Bootstrap 5
- **Formulario** para crear/editar categorías
- **Tabla** con listado de categorías y cantidad de productos
- **Estadísticas** en tiempo real
- **Mensajes** de éxito/error con auto-cierre
- **Protección**: No permite eliminar categorías con productos

### 4. **Integración con Productos**

#### Mejoras en productoForm.html
Ubicación: `src/main/resources/templates/fragments/productoForm.html`

Nuevas características:
- **Botón "+**" para agregar categoría sin salir del formulario
- **Enlace directo** a gestión de categorías (abre en nueva pestaña)
- **Campo dinámico** que aparece para crear categoría rápida
- **JavaScript** para agregar categoría al select automáticamente

#### Mejoras en AdminController
Ubicación: `src/main/java/com/example/Ejemplo/controllers/AdminController.java`

Método `subirproductos` mejorado con:
- **Validación** de campos obligatorios
- **Creación automática** de categoría si no existe
- **Manejo de errores** robusto con mensajes claros
- **Trim** de espacios en nombres
- **Validación** de precio y stock

### 5. **Seguridad**

#### SecurityConfig
Ubicación: `src/main/java/com/example/Ejemplo/security/SecurityConfig.java`

- Agregado endpoint `/categorias/**` para roles ADMINISTRADOR y TRABAJADOR
- Protección de API REST de categorías

### 6. **Navegación**

#### navbarAdmin.html
Ubicación: `src/main/resources/templates/fragments/navbarAdmin.html`

- Agregado enlace "Categorías" con icono de carpeta
- Mejora de iconos en otros enlaces

### 7. **Recursos Estáticos**

#### categoriaProducto.js
Ubicación: `src/main/resources/static/js/categoriaProducto.js`

Funcionalidades:
- Mostrar/ocultar campo de nueva categoría
- Validar nombre no vacío y no duplicado
- Agregar categoría al select dinámicamente
- Soporte para tecla Enter
- Mensajes de feedback al usuario

#### categorias.css
Ubicación: `src/main/resources/static/css/categorias.css`

Estilos personalizados:
- Cards con hover effects
- Animaciones de entrada para alertas
- Diseño responsivo
- Mejoras visuales para inputs

## Flujo de Trabajo

### Crear una Categoría

**Opción 1: Desde Gestión de Categorías**
1. Ir a menú lateral → "Categorías"
2. Completar nombre en el formulario
3. Clic en "Guardar"
4. Se muestra mensaje de éxito

**Opción 2: Desde Formulario de Producto**
1. Ir a "Productos" → Formulario
2. Clic en botón "+" junto al select de categoría
3. Escribir nombre de categoría
4. Clic en "Guardar"
5. La categoría se agrega automáticamente al select

### Editar una Categoría

1. Ir a "Categorías"
2. Clic en botón "Editar" (icono lápiz)
3. Modificar nombre
4. Clic en "Actualizar"

### Eliminar una Categoría

1. Ir a "Categorías"
2. Verificar que no tenga productos asociados (badge de productos = 0)
3. Clic en botón "Eliminar" (icono basura)
4. Confirmar en el diálogo
5. Si tiene productos, aparece botón bloqueado (candado)

### Crear Producto con Categoría

1. Ir a "Productos"
2. Completar formulario
3. Seleccionar categoría existente o crear nueva
4. Al guardar, la categoría se vincula automáticamente

## Validaciones Implementadas

### Backend
- ✅ Nombre de categoría no puede estar vacío
- ✅ No permite categorías con nombres duplicados
- ✅ No permite eliminar categorías con productos asociados
- ✅ Validación de precio > 0 en productos
- ✅ Validación de stock >= 0 en productos
- ✅ Trim automático de espacios en nombres

### Frontend
- ✅ Campos requeridos en formularios
- ✅ Validación de categoría duplicada en JavaScript
- ✅ Confirmación antes de eliminar
- ✅ Deshabilitar botón eliminar si hay productos

## Mejoras de UX

- 📱 **Responsivo**: Funciona en móviles y tablets
- ⚡ **Rápido**: JavaScript maneja acciones sin recargar
- 🎨 **Visual**: Animaciones suaves y feedback inmediato
- 🔒 **Seguro**: Protección en backend y frontend
- 📊 **Informativo**: Estadísticas en tiempo real
- ✨ **Intuitivo**: Iconos claros y mensajes descriptivos

## Estructura de Base de Datos

La entidad `Categoria` se relaciona con `Producto`:

```java
@Entity
@Table(name = "categorias")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCategoria;
    
    @Column(nullable = false)
    private String nombre;
    
    @OneToMany(mappedBy = "categoria")
    private List<Producto> productos;
}
```

## Permisos Requeridos

- **Acceso**: ADMINISTRADOR y TRABAJADOR
- **Rutas protegidas**: `/categorias/**`

## Notas Técnicas

- Spring Boot + Thymeleaf
- Bootstrap 5 + Bootstrap Icons
- JPA/Hibernate para persistencia
- Spring Security para autenticación
- JavaScript vanilla (sin frameworks)

## Testing Recomendado

1. ✅ Crear categoría básica
2. ✅ Crear categoría con mismo nombre (debe fallar)
3. ✅ Crear producto con categoría existente
4. ✅ Crear producto con categoría nueva
5. ✅ Editar nombre de categoría
6. ✅ Intentar eliminar categoría con productos (debe fallar)
7. ✅ Eliminar categoría sin productos
8. ✅ Crear categoría desde formulario de producto

## Próximas Mejoras (Opcional)

- [ ] Agregar descripción a categorías
- [ ] Permitir imágenes para categorías
- [ ] Ordenamiento de productos por categoría
- [ ] Filtros avanzados en listado
- [ ] Exportar categorías a CSV/Excel
- [ ] Importar categorías masivamente
- [ ] Árbol de categorías (subcategorías)

---

**Desarrollado para**: UTPedidos  
**Fecha**: Octubre 2025  
**Tecnologías**: Spring Boot, Thymeleaf, Bootstrap 5, JavaScript
