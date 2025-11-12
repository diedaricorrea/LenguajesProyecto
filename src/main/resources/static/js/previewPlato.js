document.addEventListener('DOMContentLoaded', function () {
    // Obtener referencias a los elementos del formulario
    const nombreInput = document.getElementById('nombre');
    const precioInput = document.getElementById('precio');
    const descripcionInput = document.getElementById('descripcion');
    const categoriaSelect = document.getElementById('categoria');
    const imagenInput = document.getElementById('imagen');
    const disponibleCheck = document.getElementById('disponible');

    // Obtener referencias a los elementos de la vista previa
    const previewNombre = document.getElementById('previewNombre');
    const previewPrecio = document.getElementById('previewPrecio');
    const previewDescripcion = document.getElementById('previewDescripcion');
    const previewCategoria = document.getElementById('previewCategoria');
    const previewImagen = document.getElementById('previewImagen');
    const previewDisponibilidad = document.getElementById('previewDisponibilidad');
    const IGV = 0.18;
    // Función para actualizar la vista previa
    function actualizarVistaPrevia() {
        // Actualizar nombre
        previewNombre.textContent = nombreInput.value || 'Nombre del Plato';

        // Actualizar precio
        previewPrecio.textContent = 'S/ ' + (((precioInput.value) - (precioInput.value * IGV)).toFixed(2) || '0.00');

        // Actualizar descripción
        previewDescripcion.textContent = descripcionInput.value || 'Descripción breve del plato...';

        // Actualizar categoría
        if (categoriaSelect.value !== 'Seleccionar categoría') {
            previewCategoria.textContent = categoriaSelect.value;
        } else {
            previewCategoria.textContent = 'Categoría';
        }

        // Actualizar disponibilidad
        if (disponibleCheck.checked) {
            previewDisponibilidad.textContent = 'Disponible';
            previewDisponibilidad.classList.remove('no-disponible-badge');
            previewDisponibilidad.classList.add('disponible-badge');
        } else {
            previewDisponibilidad.textContent = 'No Disponible';
            previewDisponibilidad.classList.remove('disponible-badge');
            previewDisponibilidad.classList.add('no-disponible-badge');
        }
    }

    // Función para manejar la vista previa de la imagen
    function actualizarImagenPreview() {
        const file = imagenInput.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                previewImagen.src = e.target.result;
                previewImagen.style.display = 'block';
            };
            reader.readAsDataURL(file);
        }
    }

    // Agregar event listeners a los campos del formulario
    nombreInput.addEventListener('input', actualizarVistaPrevia);
    precioInput.addEventListener('input', actualizarVistaPrevia);
    descripcionInput.addEventListener('input', actualizarVistaPrevia);
    categoriaSelect.addEventListener('change', actualizarVistaPrevia);
    disponibleCheck.addEventListener('change', actualizarVistaPrevia);
    imagenInput.addEventListener('change', actualizarImagenPreview);

    // Inicializar la vista previa con los valores actuales
    actualizarVistaPrevia();
});