-- Script de migración para actualizar usuarios existentes con el nuevo sistema de roles
-- Este script asume que las tablas roles, permisos y rol_permiso ya fueron creadas por Hibernate

-- Verificar que existan las tablas necesarias
-- SELECT * FROM roles;
-- SELECT * FROM permisos;
-- SELECT * FROM rol_permiso;

-- Actualizar usuarios que tienen rol ADMINISTRADOR (enum) pero no tienen rolEntity
UPDATE usuarios u
SET id_rol_entity = (SELECT id_rol FROM roles WHERE nombre = 'ADMINISTRADOR')
WHERE u.rol = 'ADMINISTRADOR' 
  AND u.id_rol_entity IS NULL;

-- Actualizar usuarios que tienen rol TRABAJADOR (enum) pero no tienen rolEntity
UPDATE usuarios u
SET id_rol_entity = (SELECT id_rol FROM roles WHERE nombre = 'TRABAJADOR')
WHERE u.rol = 'TRABAJADOR' 
  AND u.id_rol_entity IS NULL;

-- Actualizar usuarios que tienen rol USUARIO (enum) pero no tienen rolEntity
UPDATE usuarios u
SET id_rol_entity = (SELECT id_rol FROM roles WHERE nombre = 'USUARIO')
WHERE u.rol = 'USUARIO' 
  AND u.id_rol_entity IS NULL;

-- Verificar la migración
SELECT 
    u.id_usuario,
    u.nombre,
    u.correo,
    u.rol as rol_enum,
    r.nombre as rol_entity,
    COUNT(p.id_permiso) as cantidad_permisos
FROM usuarios u
LEFT JOIN roles r ON u.id_rol_entity = r.id_rol
LEFT JOIN rol_permiso rp ON r.id_rol = rp.id_rol
LEFT JOIN permisos p ON rp.id_permiso = p.id_permiso
GROUP BY u.id_usuario, u.nombre, u.correo, u.rol, r.nombre
ORDER BY u.id_usuario;
