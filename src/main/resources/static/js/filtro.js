document.addEventListener("DOMContentLoaded", function () {
    const btnMenuDia = document.getElementById("btn-menu-dia");
    const botonesMenuDia = document.getElementById("botones-menu-dia");
    const filtrosGenerales = document.querySelectorAll(".filtro-general");

    // Leer parámetro de categoría desde URL
    function getCategoria() {
        const params = new URLSearchParams(window.location.search);
        return params.get("categoria")?.toUpperCase() || "";
    }

    const categoria = getCategoria();
    const esMenuDia = ["MENU ECONOMICO", "MENU EJECUTIVO", "MENU UNIVERSITARIO"].includes(categoria);

    // Mostrar subcategorías si se está en alguna del menú del día
    if (esMenuDia) {
        botonesMenuDia.classList.remove("d-none");
        btnMenuDia.classList.add("activo");
    }

    // Mostrar/ocultar subcategorías al hacer clic en MENÚ DEL DÍA
    btnMenuDia.addEventListener("click", function (e) {
        e.preventDefault(); // evitar redirección
        botonesMenuDia.classList.toggle("d-none");
    });

    // Ocultar subcategorías si se hace clic en otras categorías
    filtrosGenerales.forEach(boton => {
        boton.addEventListener("click", function () {
            // Ocultar las subcategorías si el usuario cambia de categoría
            botonesMenuDia.classList.add("d-none");
        });
    });
});