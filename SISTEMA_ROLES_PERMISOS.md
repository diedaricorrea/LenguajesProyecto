# Sistema de Roles y Permisos - UTPedidos

## 📋 Descripción General

Este proyecto implementa un **sistema robusto de autenticación y autorización** basado en roles y permisos granulares. El sistema permite una gestión flexible de accesos y privilegios para diferentes tipos de usuarios.

## 🏗️ Arquitectura

### Modelos Principales

#### 1. **RolEntity** (`models/RolEntity.java`)
- Entidad que representa los roles del sistema
- Relación **Muchos a Muchos** con Permiso
- Relación **Uno a Muchos** con Usuario

#### 2. **Permiso** (`models/Permiso.java`)
- Entidad que representa permisos específicos
- Agrupados por módulos (PRODUCTOS, CATEGORIAS, PEDIDOS, etc.)
- Relación **Muchos a Muchos** con RolEntity

#### 3. **Usuario** (actualizado)
- Mantiene el campo `rol` (enum) para compatibilidad
- Nuevo campo `rolEntity` que apunta a RolEntity
- Métodos auxiliares:
  - `getRolNombre()`: Obtiene el nombre del rol efectivo
  - `tienePermiso(String)`: Verifica si tiene un permiso específico

## 🔐 Roles Predefinidos

### ADMINISTRADOR
- **Acceso total** al sistema
- Todos los permisos disponibles
- Puede gestionar usuarios, roles y permisos

### TRABAJADOR
- Acceso operativo completo
- Puede gestionar productos, categorías, pedidos, ventas
- Ver estadísticas
- Gestionar menú del día y notificaciones
- **NO puede**: eliminar productos o gestionar usuarios

### USUARIO
- Acceso básico de cliente
- Ver productos y categorías
- Crear y ver sus propios pedidos
- Gestionar su carrito
- Ver notificaciones

## 🎯 Permisos por Módulo

### PRODUCTOS
- `PRODUCTOS_VER` - Ver catálogo
- `PRODUCTOS_CREAR` - Crear productos
- `PRODUCTOS_EDITAR` - Editar productos
- `PRODUCTOS_ELIMINAR` - Eliminar productos ⚠️ (Solo Admin)
- `PRODUCTOS_GESTIONAR` - Gestión completa

### CATEGORÍAS
- `CATEGORIAS_VER` - Ver categorías
- `CATEGORIAS_CREAR` - Crear categorías
- `CATEGORIAS_EDITAR` - Editar categorías
- `CATEGORIAS_ELIMINAR` - Eliminar categorías ⚠️ (Solo Admin)

### PEDIDOS
- `PEDIDOS_VER` - Ver pedidos
- `PEDIDOS_CREAR` - Crear pedidos
- `PEDIDOS_GESTIONAR` - Gestionar todos los pedidos
- `PEDIDOS_ACTUALIZAR_ESTADO` - Cambiar estado de pedidos

### USUARIOS
- `USUARIOS_VER` - Ver usuarios ⚠️
- `USUARIOS_CREAR` - Crear usuarios ⚠️
- `USUARIOS_EDITAR` - Editar usuarios ⚠️
- `USUARIOS_ELIMINAR` - Eliminar usuarios ⚠️
- `USUARIOS_GESTIONAR_ROLES` - Asignar roles ⚠️

*(⚠️ = Solo Administrador)*

### Otros Módulos
- **VENTAS**: Ver y gestionar ventas
- **ESTADISTICAS**: Ver métricas del sistema
- **NOTIFICACIONES**: Gestionar notificaciones
- **MENU_DIA**: Gestionar menú del día
- **CARRITO**: Gestionar carrito de compras

## 🔧 Configuración de Seguridad

### SecurityConfig
El archivo `SecurityConfig.java` configura:
- Rutas públicas (login, registro, recursos estáticos)
- Autorización basada en permisos (`hasAnyAuthority`)
- Manejo de acceso denegado (redirige a `/error/403`)
- Habilita anotaciones de seguridad (`@PreAuthorize`)

### Anotaciones en Controladores

Usa `@PreAuthorize` para proteger endpoints:

```java
@PreAuthorize("hasAnyAuthority('PRODUCTOS_VER', 'ROLE_ADMINISTRADOR')")
@GetMapping("/productos")
public String listarProductos(Model model) {
    // ...
}
```

## 📊 Flujo de Inicialización

1. **RolesPermisosInitializer** (Order 1)
   - Crea todos los permisos del sistema
   - Crea roles con sus permisos asignados
   
2. **AdminInitializer** (Order 2)
   - Crea usuario administrador por defecto
   - Asigna rol ADMINISTRADOR al usuario
   - Actualiza usuarios existentes con el nuevo sistema

## 🚀 Uso

### Verificar Permisos en Código

```java
// En un controlador
Usuario usuario = userDetails.getUsuario();
if (usuario.tienePermiso("PRODUCTOS_EDITAR")) {
    // Permitir edición
}
```

### Obtener Rol del Usuario

```java
// Método actualizado que prioriza rolEntity sobre enum
String rolNombre = usuario.getRolNombre();
model.addAttribute("usuarioRol", rolNombre);
```

### Migración de Usuarios Existentes

Los usuarios existentes se migran automáticamente al iniciar la aplicación. Si necesitas migrar manualmente, ejecuta:

```sql
-- Ver archivo: src/main/resources/db/migration/migracion_roles_permisos.sql
```

## 🔑 Credenciales por Defecto

```
Usuario: admin@cafeteria.com
Contraseña: admin123
```

⚠️ **IMPORTANTE**: Cambiar la contraseña del administrador en producción

## 📝 Mejores Prácticas

1. **Usa permisos específicos** en lugar de solo roles
2. **Combina permisos y roles** en `@PreAuthorize` para flexibilidad
3. **Registra acciones** importantes con logs
4. **Valida permisos** tanto en backend como frontend
5. **Mantén actualizada** la documentación de permisos

## 🐛 Solución de Problemas

### Error: "Access Denied"
- Verifica que el usuario tenga el rol correcto
- Confirma que el rol tiene los permisos necesarios
- Revisa los logs para ver qué autoridades tiene el usuario

### Usuario no tiene permisos después de login
- Verifica que `rolEntity` esté asignado
- Ejecuta la migración SQL si es necesario
- Reinicia la aplicación para ejecutar los inicializadores

### Página 403 no se muestra
- Verifica que existe `/templates/error/403.html`
- Confirma la configuración en `SecurityConfig`

## 📚 Referencias

- Spring Security: https://spring.io/projects/spring-security
- Spring Method Security: https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html
