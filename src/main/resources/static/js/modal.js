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
    document.getElementById('modalDetalle').style.display = 'block';
    document.getElementById('modalNombre').innerText = button.dataset.nombre;
    document.getElementById('modalPrecio').innerText = 'S/ ' + button.dataset.precio;
    document.getElementById('nombreProducto').value = button.dataset.nombre;
    document.getElementById('modalDescription').value = button.dataset.description;
    document.getElementById('idProducto').value = button.dataset.id
}

function cerrarModal() {
    document.getElementById('modalDetalle').style.display = 'none';
}