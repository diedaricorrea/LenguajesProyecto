-- Script para corregir la tabla notificaciones
-- Agregar AUTO_INCREMENT a id_notificacion

USE utpedidos;

-- Paso 1: Verificar estructura actual
SHOW CREATE TABLE notificaciones;

-- Paso 2: Modificar la columna para agregar AUTO_INCREMENT
ALTER TABLE notificaciones 
MODIFY COLUMN id_notificacion INT NOT NULL AUTO_INCREMENT;

-- Verificar que se aplicó el cambio
SHOW CREATE TABLE notificaciones;
