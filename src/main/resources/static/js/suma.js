function calcularTotal() {
    let total = 0;
    const subtotales = document.querySelectorAll('.subtotal');

    subtotales.forEach(td => {
        // Quita "S/" si lo hubiera, y convierte a número
        const valor = parseFloat(td.innerText.replace(/[^\d.]/g, ''));
        if (!isNaN(valor)) total += valor;
    });

    // Mostrar con dos decimales y prefijo "S/"
    document.getElementById('totalCarrito').innerText = 'S/ ' + total.toFixed(2);
}


// Ejecutar al cargar la página
document.addEventListener('DOMContentLoaded', calcularTotal);

function eliminarFila(boton) {
    const fila = boton.closest('tr'); // Encuentra la fila más cercana
    fila.remove(); // Elimina la fila del DOM
    calcularTotal(); // Recalcula el total del carrito
}