const modalDetalle = document.getElementById('modalFecha');

modalDetalle.addEventListener('show.bs.modal', function (event) {
    const button = event.relatedTarget;
    const nombre = button.getAttribute('data-nombre');
    const precio = button.getAttribute('data-precio');
    const id = button.getAttribute('data-id');
    const descripcion = button.getAttribute('data-description');

    const modalNombre = modalDetalle.querySelector('#modalNombre');
    const modalPrecio = modalDetalle.querySelector('#modalPrecio');
    const modalId = modalDetalle.querySelector('#modalId');
    const modalDesc = modalDetalle.querySelector('#modalDescription');
    modalId.value = id;

    modalDesc.textContent = descripcion;
    modalNombre.textContent = nombre;
    modalPrecio.textContent = 'Precio: S/. ' + precio;
    modalId.textContent = id;

});


function abrirModal(button) {
    const modalElement = document.getElementById('modalDetalle');
    if (!modalElement) {
        console.error('Modal no encontrado');
        return;
    }
    
    // Si se llama sin parámetro (desde carrito), solo abre el modal
    if (!button) {
        modalElement.style.display = 'block';
        return;
    }
    
    // Si se llama con parámetro (desde catálogo con datos del producto)
    const modal = new bootstrap.Modal(modalElement);
    if (document.getElementById('modalNombre')) {
        document.getElementById('modalNombre').innerText = button.dataset.nombre;
    }
    if (document.getElementById('modalPrecio')) {
        document.getElementById('modalPrecio').innerText = 'S/ ' + button.dataset.precio;
    }
    if (document.getElementById('nombreProducto')) {
        document.getElementById('nombreProducto').value = button.dataset.nombre;
    }
    if (document.getElementById('modalDescription')) {
        document.getElementById('modalDescription').innerText = button.dataset.description || 'Sin descripción disponible';
    }
    if (document.getElementById('idProducto')) {
        document.getElementById('idProducto').value = button.dataset.id;
    }
    if (document.getElementById('cantidadInput')) {
        document.getElementById('cantidadInput').value = 1;
    }
    modal.show();
}

function cerrarModal() {
    const modalElement = document.getElementById('modalDetalle');
    if (!modalElement) return;
    
    const modalInstance = bootstrap.Modal.getInstance(modalElement);
    if (modalInstance) {
        modalInstance.hide();
    } else {
        modalElement.style.display = 'none';
    }
}

function incrementarCantidad() {
    const input = document.getElementById('cantidadInput');
    if (!input) return;
    
    const max = parseInt(input.max);
    const current = parseInt(input.value);
    if (current < max) {
        input.value = current + 1;
    }
}

function decrementarCantidad() {
    const input = document.getElementById('cantidadInput');
    if (!input) return;
    
    const min = parseInt(input.min);
    const current = parseInt(input.value);
    if (current > min) {
        input.value = current - 1;
    }
}

function confirmarPago() {
    // Esta función se puede usar para validaciones adicionales antes del submit
    console.log('Confirmando pago...');
    return true;
}