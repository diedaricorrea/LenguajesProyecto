# 🔒 Sistema de Seguridad Implementado - UTPedidos

## ✅ Implementación Completada

### 1. **Sistema de Roles y Permisos Dinámico**

#### Roles del Sistema
- **ADMINISTRADOR**: Acceso total al sistema
- **TRABAJADOR**: Gestión operativa (productos, categorías, pedidos)
- **USUARIO**: Solo acceso a catálogo y carrito
- **Roles Personalizados**: Se pueden crear desde `/roles`

#### Permisos Disponibles
Los permisos están organizados por módulos:

**PRODUCTOS**
- `PRODUCTOS_VER`
- `PRODUCTOS_CREAR`
- `PRODUCTOS_EDITAR`
- `PRODUCTOS_ELIMINAR`
- `PRODUCTOS_GESTIONAR`

**CATEGORÍAS**
- `CATEGORIAS_VER`
- `CATEGORIAS_CREAR`
- `CATEGORIAS_EDITAR`
- `CATEGORIAS_ELIMINAR`
- `CATEGORIAS_GESTIONAR`

**PEDIDOS**
- `PEDIDOS_VER`
- `PEDIDOS_CREAR`
- `PEDIDOS_ACTUALIZAR_ESTADO`
- `PEDIDOS_GESTIONAR`

**USUARIOS**
- `USUARIOS_VER`
- `USUARIOS_CREAR`
- `USUARIOS_EDITAR`
- `USUARIOS_ELIMINAR`
- `USUARIOS_GESTIONAR_ROLES`

**MENÚ DEL DÍA**
- `MENU_DIA_VER`
- `MENU_DIA_GESTIONAR`

**ESTADÍSTICAS Y VENTAS**
- `ESTADISTICAS_VER`
- `ESTADISTICAS_PRODUCTOS`
- `ESTADISTICAS_VENTAS`
- `VENTAS_VER`
- `VENTAS_CREAR`
- `VENTAS_GESTIONAR`

**NOTIFICACIONES**
- `NOTIFICACIONES_VER`
- `NOTIFICACIONES_CREAR`
- `NOTIFICACIONES_GESTIONAR`

**CARRITO**
- `CARRITO_GESTIONAR`

---

### 2. **Protección a Nivel de Controlador**

Todos los controladores admin están protegidos con `@PreAuthorize`:

```java
// ProductoAdminController
@PreAuthorize("hasAnyAuthority('PRODUCTOS_VER', 'PRODUCTOS_GESTIONAR', 'ROLE_ADMINISTRADOR')")

// CategoriaAdminController  
@PreAuthorize("hasAnyAuthority('CATEGORIAS_VER', 'CATEGORIAS_GESTIONAR', 'ROLE_ADMINISTRADOR')")

// PedidoAdminController
@PreAuthorize("hasAnyAuthority('PEDIDOS_VER', 'PEDIDOS_GESTIONAR', 'ROLE_ADMINISTRADOR')")

// MenuDiaController
@PreAuthorize("hasAnyAuthority('MENU_DIA_VER', 'MENU_DIA_GESTIONAR', 'ROLE_ADMINISTRADOR')")

// VentasController
@PreAuthorize("hasAnyAuthority('ESTADISTICAS_VER', 'VENTAS_VER', 'ROLE_ADMINISTRADOR')")

// RolController (Solo ADMINISTRADOR)
@PreAuthorize("hasRole('ADMINISTRADOR')")

// UsuariosController (Solo ADMINISTRADOR)
@PreAuthorize("hasRole('ADMINISTRADOR')")
```

---

### 3. **Sidebar Adaptativa con Spring Security**

El navbar usa `sec:authorize` para mostrar solo las opciones permitidas:

```html
<!-- Productos -->
<li sec:authorize="hasAnyAuthority('PRODUCTOS_VER', 'PRODUCTOS_GESTIONAR', 'ROLE_ADMINISTRADOR')">
    <a href="/admin/productos">Gestión de Productos</a>
</li>

<!-- Categorías -->
<li sec:authorize="hasAnyAuthority('CATEGORIAS_VER', 'CATEGORIAS_GESTIONAR', 'ROLE_ADMINISTRADOR')">
    <a href="/admin/categorias">Gestión de Categorías</a>
</li>

<!-- Pedidos -->
<li sec:authorize="hasAnyAuthority('PEDIDOS_VER', 'PEDIDOS_GESTIONAR', 'ROLE_ADMINISTRADOR')">
    <a href="/admin/pedidos">Gestión de Pedidos</a>
</li>

<!-- Usuarios (Solo Admin) -->
<li sec:authorize="hasRole('ADMINISTRADOR')">
    <a href="/usuarios/panelAdmin">Usuarios</a>
</li>

<!-- Roles y Permisos (Solo Admin) -->
<li sec:authorize="hasRole('ADMINISTRADOR')">
    <a href="/roles">Roles y Permisos</a>
</li>
```

**Comportamiento:**
- ✅ **Usuario ve solo lo que puede usar**
- ✅ **UI limpia y adaptada al rol**
- ✅ **Sin confusión ni errores de "Sin permiso"**

---

### 4. **Gestión de Roles desde UI**

#### Crear Rol
1. Ir a `/roles`
2. Click en "Nuevo Rol"
3. Ingresar nombre y descripción
4. Guardar

#### Asignar Permisos
1. Click en el icono 🔑 del rol
2. Seleccionar permisos por módulo
3. Guardar

#### Asignar Rol a Usuario
1. Ir a `/usuarios/panelAdmin`
2. Crear o editar usuario
3. **Ahora aparecen todos los roles** (dinámicamente desde BD)
4. Seleccionar el rol deseado

---

### 5. **Ejemplo de Uso: Rol "Gestor de Productos"**

#### Creación
```
Nombre: GESTOR_PRODUCTOS
Descripción: Solo gestiona productos y categorías
Permisos asignados:
  - PRODUCTOS_VER
  - PRODUCTOS_CREAR
  - PRODUCTOS_EDITAR
  - CATEGORIAS_VER
  - CATEGORIAS_CREAR
```

#### Resultado
Un usuario con este rol verá en el sidebar **SOLAMENTE**:
- ✅ Gestión de Productos
- ✅ Gestión de Categorías
- ✅ Salir

**NO verá:**
- ❌ Pedidos
- ❌ Usuarios
- ❌ Roles
- ❌ Dashboard
- ❌ Menú del día

---

### 6. **Seguridad en Capas**

#### Capa 1: Controller (`@PreAuthorize`)
```java
@PreAuthorize("hasAnyAuthority('PRODUCTOS_VER', 'ROLE_ADMINISTRADOR')")
public String listarProductos() { ... }
```
**Protege:** Acceso directo por URL

#### Capa 2: UI (`sec:authorize`)
```html
<li sec:authorize="hasAuthority('PRODUCTOS_VER')">
    <a href="/admin/productos">Productos</a>
</li>
```
**Protege:** Visibilidad en la interfaz

#### Capa 3: Service (Opcional)
```java
@PreAuthorize("hasAuthority('PRODUCTOS_ELIMINAR')")
public void eliminar(Long id) { ... }
```
**Protege:** Lógica de negocio

---

### 7. **Agregar Nuevos Permisos**

Para agregar permisos para un nuevo módulo:

1. **Editar `RolesPermisosInitializer.java`**
```java
private void crearPermisosNuevoModulo() {
    crearPermiso("NUEVO_MODULO_VER", "Ver módulo nuevo", "NUEVO_MODULO");
    crearPermiso("NUEVO_MODULO_CREAR", "Crear en módulo nuevo", "NUEVO_MODULO");
    crearPermiso("NUEVO_MODULO_EDITAR", "Editar en módulo nuevo", "NUEVO_MODULO");
    crearPermiso("NUEVO_MODULO_ELIMINAR", "Eliminar en módulo nuevo", "NUEVO_MODULO");
}
```

2. **Proteger el Controller**
```java
@Controller
@RequestMapping("/admin/nuevo-modulo")
@PreAuthorize("hasAnyAuthority('NUEVO_MODULO_VER', 'ROLE_ADMINISTRADOR')")
public class NuevoModuloController { ... }
```

3. **Agregar al Navbar**
```html
<li sec:authorize="hasAnyAuthority('NUEVO_MODULO_VER', 'ROLE_ADMINISTRADOR')">
    <a href="/admin/nuevo-modulo">Nuevo Módulo</a>
</li>
```

4. **Reiniciar aplicación** para crear los permisos en BD

---

### 8. **Archivos Modificados**

#### Controllers Protegidos
- ✅ `ProductoAdminController.java`
- ✅ `CategoriaAdminController.java`
- ✅ `PedidoAdminController.java`
- ✅ `MenuDiaController.java`
- ✅ `VentasController.java`
- ✅ `RolController.java`
- ✅ `UsuariosController.java`

#### Templates Actualizados
- ✅ `navbarAdmin.html` - Sidebar con `sec:authorize`
- ✅ `usuariosAdmin.html` - Roles dinámicos en formularios
- ✅ `rolesLista.html` - Gestión de roles
- ✅ `asignarPermisos.html` - Asignación de permisos

#### Configuración
- ✅ `SecurityConfig.java` - Spring Security configurado
- ✅ `RolesPermisosInitializer.java` - Datos iniciales

---

### 9. **Testing de Seguridad**

#### Probar con usuario "GESTOR_PRODUCTOS"
1. Crear rol `GESTOR_PRODUCTOS`
2. Asignar solo permisos de productos
3. Crear usuario con ese rol
4. Login con ese usuario
5. Verificar que:
   - ✅ Solo ve "Productos" en sidebar
   - ✅ Puede acceder a `/admin/productos`
   - ❌ No puede acceder a `/admin/pedidos` (403)
   - ❌ No ve "Pedidos" en sidebar

---

### 10. **Beneficios de la Implementación**

✅ **Seguridad Robusta**: Múltiples capas de protección  
✅ **Escalabilidad**: Fácil agregar nuevos roles y permisos  
✅ **UX Mejorada**: UI adaptada al rol del usuario  
✅ **Mantenibilidad**: Sistema centralizado y bien estructurado  
✅ **Flexibilidad**: Permisos granulares por acción  
✅ **Auditoría**: Rastreo claro de quién puede hacer qué  

---

## 🎯 Estado Final del Sistema

| Componente | Estado | Descripción |
|------------|--------|-------------|
| **Roles** | ✅ Completo | CRUD completo desde UI |
| **Permisos** | ✅ Completo | 30+ permisos por módulo |
| **Asignación** | ✅ Completo | Asignar permisos a roles |
| **Sidebar Dinámico** | ✅ Completo | Oculta opciones sin permiso |
| **Controllers** | ✅ Protegidos | Todos con @PreAuthorize |
| **Usuarios** | ✅ Completo | Asignación dinámica de roles |
| **Testing** | ⚠️ Manual | Probar con diferentes roles |

---

## 📝 Próximos Pasos Opcionales

1. **Auditoría**: Registrar acciones de usuarios
2. **Caducidad**: Roles temporales con fecha de expiración
3. **Jerarquía**: Roles que heredan de otros roles
4. **API REST**: Endpoints para gestión externa de permisos
5. **Tests Unitarios**: Probar seguridad automáticamente

---

---

## 🔧 Corrección de Errores Importantes

### ❌ Error: "No enum constant com.example.Ejemplo.models.Rol.GESTOR DE PRODUCTOS"

**Causa:** El `UsuarioMapper` estaba intentando convertir nombres de roles dinámicos (como "GESTOR DE PRODUCTOS") al enum `Rol` usando `Rol.valueOf()`, pero el enum solo contiene: `USUARIO`, `ADMINISTRADOR`, `TRABAJADOR`.

**Solución Implementada:**

1. **Modificado `UsuarioMapper.java`:**
   - Inyectado `RolEntityRepository` para buscar roles dinámicos
   - Cambiado `toEntity()` para buscar `RolEntity` por nombre
   - Cambiado `updateEntity()` para buscar `RolEntity` por nombre
   - Agregado fallback al enum `Rol.USUARIO` para compatibilidad

```java
// ANTES (❌ Error)
usuario.setRol(Rol.valueOf(dto.getRol().toUpperCase()));

// DESPUÉS (✅ Correcto)
RolEntity rolEntity = rolEntityRepository.findByNombre(dto.getRol())
    .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + dto.getRol()));
usuario.setRolEntity(rolEntity);

// Compatibilidad con enum (fallback)
try {
    usuario.setRol(Rol.valueOf(dto.getRol().toUpperCase()));
} catch (IllegalArgumentException e) {
    usuario.setRol(Rol.USUARIO); // Fallback seguro
}
```

2. **Sistema Dual de Roles:**
   - `Usuario.rol` (enum): Mantiene compatibilidad con código legacy
   - `Usuario.rolEntity` (RolEntity): Sistema dinámico prioritario
   - `Usuario.getRolNombre()`: Prioriza `rolEntity` sobre enum

**Resultado:**
✅ Ahora puedes crear roles con cualquier nombre  
✅ Los usuarios se pueden asignar a roles personalizados  
✅ No más errores "No enum constant"  
✅ Compatibilidad total con el sistema antiguo

---

**Documentación creada:** 19 de Noviembre, 2025  
**Última actualización:** 19 de Noviembre, 2025 - 12:25 PM  
**Sistema:** UTPedidos - Gestión de Cafetería  
**Framework:** Spring Boot 3.4.11 + Spring Security 6.4.12
