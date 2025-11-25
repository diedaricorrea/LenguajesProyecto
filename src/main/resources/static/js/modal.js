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
    const modal = new bootstrap.Modal(document.getElementById('modalDetalle'));
    document.getElementById('modalNombre').innerText = button.dataset.nombre;
    document.getElementById('modalPrecio').innerText = 'S/ ' + button.dataset.precio;
    document.getElementById('nombreProducto').value = button.dataset.nombre;
    document.getElementById('modalDescription').innerText = button.dataset.description || 'Sin descripción disponible';
    document.getElementById('idProducto').value = button.dataset.id;
    document.getElementById('cantidadInput').value = 1;
    modal.show();
}

function cerrarModal() {
    const modal = bootstrap.Modal.getInstance(document.getElementById('modalDetalle'));
    if (modal) {
        modal.hide();
    }
}

function incrementarCantidad() {
    const input = document.getElementById('cantidadInput');
    const max = parseInt(input.max);
    const current = parseInt(input.value);
    if (current < max) {
        input.value = current + 1;
    }
}

function decrementarCantidad() {
    const input = document.getElementById('cantidadInput');
    const min = parseInt(input.min);
    const current = parseInt(input.value);
    if (current > min) {
        input.value = current - 1;
    }
}

function cerrarModal() {
    document.getElementById('modalDetalle').style.display = 'none';
}