/**
 * Utilidades para SweetAlert2
 * Funciones reutilizables para mostrar alertas y confirmaciones
 */

/**
 * Muestra una confirmación para avanzar el estado de un pedido
 * @param {HTMLFormElement} form - El formulario a enviar si se confirma
 * @param {string} nuevoEstado - El nombre del nuevo estado
 * @returns {boolean} false para prevenir el submit por defecto
 */
function confirmarAvanzarPedido(form, nuevoEstado) {
    event.preventDefault();
    
    Swal.fire({
        title: '¿Avanzar este pedido?',
        text: `Se marcará como "${nuevoEstado}"`,
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#0d6efd',
        cancelButtonColor: '#6c757d',
        confirmButtonText: 'Sí, avanzar',
        cancelButtonText: 'Cancelar',
        reverseButtons: true
    }).then((result) => {
        if (result.isConfirmed) {
            form.submit();
        }
    });
    
    return false;
}

/**
 * Muestra una confirmación para desactivar un producto
 * @param {HTMLFormElement} form - El formulario a enviar si se confirma
 * @returns {boolean} false para prevenir el submit por defecto
 */
function confirmarDesactivarProducto(form) {
    event.preventDefault();
    
    Swal.fire({
        title: '¿Desactivar este producto?',
        text: 'El producto dejará de estar disponible',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#dc3545',
        cancelButtonColor: '#6c757d',
        confirmButtonText: 'Sí, desactivar',
        cancelButtonText: 'Cancelar',
        reverseButtons: true
    }).then((result) => {
        if (result.isConfirmed) {
            form.submit();
        }
    });
    
    return false;
}

/**
 * Muestra una confirmación para eliminar una categoría
 * @param {HTMLFormElement} form - El formulario a enviar si se confirma
 * @returns {boolean} false para prevenir el submit por defecto
 */
function confirmarEliminarCategoria(form) {
    event.preventDefault();
    
    Swal.fire({
        title: '¿Eliminar esta categoría?',
        text: 'Esta acción no se puede deshacer',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#dc3545',
        cancelButtonColor: '#6c757d',
        confirmButtonText: 'Sí, eliminar',
        cancelButtonText: 'Cancelar',
        reverseButtons: true
    }).then((result) => {
        if (result.isConfirmed) {
            form.submit();
        }
    });
    
    return false;
}

/**
 * Muestra un mensaje de error
 * @param {string} mensaje - El mensaje de error a mostrar
 */
function mostrarError(mensaje) {
    Swal.fire({
        icon: 'error',
        title: 'Error',
        text: mensaje,
        confirmButtonColor: '#0d6efd'
    });
}

/**
 * Muestra un mensaje de éxito
 * @param {string} mensaje - El mensaje de éxito a mostrar
 */
function mostrarExito(mensaje) {
    Swal.fire({
        icon: 'success',
        title: 'Éxito',
        text: mensaje,
        confirmButtonColor: '#0d6efd',
        timer: 2000,
        timerProgressBar: true
    });
}

/**
 * Muestra un mensaje de advertencia
 * @param {string} mensaje - El mensaje de advertencia a mostrar
 */
function mostrarAdvertencia(mensaje) {
    Swal.fire({
        icon: 'warning',
        title: 'Advertencia',
        text: mensaje,
        confirmButtonColor: '#0d6efd'
    });
}
