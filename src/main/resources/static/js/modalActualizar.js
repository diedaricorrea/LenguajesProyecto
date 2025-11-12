document.addEventListener('DOMContentLoaded', function () {
    const botonesActualizar = document.querySelectorAll('.btn-actualizar');

    botonesActualizar.forEach(boton => {
        boton.addEventListener('click', function () {
            const fila = boton.closest('tr');
            const celdas = fila.querySelectorAll('td');

            // Rellenar los campos del modal
            document.getElementById('actId').value = celdas[0].textContent.trim();
            document.getElementById('actNombre').value = celdas[1].textContent.trim();
            document.getElementById('actRol').value = celdas[3].textContent.trim();
            const estadoTexto = celdas[5].textContent.trim();

            if (estadoTexto === "TRABAJA") {
                document.getElementById('actEstado').value = "true";
            } else if (estadoTexto === "NO TRABAJA") {
                document.getElementById('actEstado').value = "false";
            }
        });
    });
});