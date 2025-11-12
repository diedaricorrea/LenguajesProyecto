// Gestión mejorada de categorías en el formulario de productos

document.addEventListener('DOMContentLoaded', function() {
    // ========== ELEMENTOS DEL DOM ==========
    const categoriaSearch = document.getElementById('categoriaSearch');
    const categoriaSelect = document.getElementById('categoria');
    const sugerenciasDiv = document.getElementById('categoriasSugerencias');
    const categoriaSeleccionada = document.getElementById('categoriaSeleccionada');
    const categoriaTexto = document.getElementById('categoriaTexto');
    const btnLimpiarCategoria = document.getElementById('btnLimpiarCategoria');
    const btnCrearCategoriaRapida = document.getElementById('btnCrearCategoriaRapida');
    
    // Modal
    const modal = document.getElementById('modalNuevaCategoria');
    const btnCerrarModal = document.getElementById('btnCerrarModalCategoria');
    const btnCancelarModal = document.getElementById('btnCancelarModalCategoria');
    const btnConfirmarNuevaCategoria = document.getElementById('btnConfirmarNuevaCategoria');
    const nuevaCategoriaNombre = document.getElementById('nuevaCategoriaNombre');
    
    // Formulario
    const productoForm = document.getElementById('productoForm');
    const nombreInput = document.getElementById('nombre');
    const precioInput = document.getElementById('precio');
    const descripcionTextarea = document.getElementById('descripcion');
    const stockInput = document.getElementById('stock');
    const imagenInput = document.getElementById('imagen');
    const disponibleCheckbox = document.getElementById('disponible');
    
    // Botones de stock
    const btnRestarStock = document.getElementById('btnRestarStock');
    const btnSumarStock = document.getElementById('btnSumarStock');
    
    // Drag & Drop
    const dropZone = document.getElementById('imagenDropZone');
    const imagenPlaceholder = document.getElementById('imagenPlaceholder');
    const imagenPreview = document.getElementById('imagenPreview');
    const imagenPreviewImg = document.getElementById('imagenPreviewImg');
    const btnEliminarImagen = document.getElementById('btnEliminarImagen');
    
    // Estado
    let categorias = Array.from(categoriaSelect.options)
        .filter(opt => opt.value !== '')
        .map(opt => opt.value);
    
    // ========== BÚSQUEDA Y AUTOCOMPLETADO DE CATEGORÍAS ==========
    
    categoriaSearch.addEventListener('input', function() {
        const busqueda = this.value.trim().toLowerCase();
        
        if (busqueda === '') {
            sugerenciasDiv.style.display = 'none';
            return;
        }
        
        // Filtrar categorías existentes
        const categoriasFiltradas = categorias.filter(cat => 
            cat.toLowerCase().includes(busqueda)
        );
        
        // Crear HTML de sugerencias
        let html = '';
        
        if (categoriasFiltradas.length > 0) {
            categoriasFiltradas.forEach(cat => {
                html += `
                    <div class="categoria-sugerencia-item" data-categoria="${cat}">
                        <i class="bi bi-bookmark"></i>
                        <span>${cat}</span>
                    </div>
                `;
            });
        }
        
        // Opción para crear nueva categoría
        html += `
            <div class="categoria-sugerencia-item crear-nueva" data-crear="${this.value.trim()}">
                <i class="bi bi-plus-circle"></i>
                <span>Crear "<strong>${this.value.trim()}</strong>"</span>
            </div>
        `;
        
        sugerenciasDiv.innerHTML = html;
        sugerenciasDiv.style.display = 'block';
        
        // Eventos de click en sugerencias
        sugerenciasDiv.querySelectorAll('.categoria-sugerencia-item').forEach(item => {
            item.addEventListener('click', function() {
                if (this.classList.contains('crear-nueva')) {
                    const nombreNueva = this.dataset.crear;
                    abrirModalNuevaCategoria(nombreNueva);
                } else {
                    seleccionarCategoria(this.dataset.categoria);
                }
            });
        });
    });
    
    // Cerrar sugerencias al hacer click fuera
    document.addEventListener('click', function(e) {
        if (!categoriaSearch.contains(e.target) && !sugerenciasDiv.contains(e.target)) {
            sugerenciasDiv.style.display = 'none';
        }
    });
    
    // ========== SELECCIÓN DE CATEGORÍA ==========
    
    function seleccionarCategoria(nombreCategoria) {
        categoriaSelect.value = nombreCategoria;
        categoriaTexto.textContent = nombreCategoria;
        categoriaSeleccionada.style.display = 'block';
        categoriaSearch.value = '';
        sugerenciasDiv.style.display = 'none';
        
        // Validación visual
        categoriaSelect.classList.add('is-valid');
        categoriaSelect.classList.remove('is-invalid');
    }
    
    btnLimpiarCategoria.addEventListener('click', function() {
        categoriaSelect.value = '';
        categoriaSeleccionada.style.display = 'none';
        categoriaSearch.value = '';
        categoriaSelect.classList.remove('is-valid');
    });
    
    // Verificar si hay categoría pre-seleccionada (al editar)
    if (categoriaSelect.value !== '') {
        seleccionarCategoria(categoriaSelect.value);
    }
    
    // ========== MODAL NUEVA CATEGORÍA ==========
    
    btnCrearCategoriaRapida.addEventListener('click', function() {
        abrirModalNuevaCategoria('');
    });
    
    function abrirModalNuevaCategoria(nombrePredeterminado) {
        modal.style.display = 'flex';
        nuevaCategoriaNombre.value = nombrePredeterminado;
        nuevaCategoriaNombre.focus();
    }
    
    function cerrarModalNuevaCategoria() {
        modal.style.display = 'none';
        nuevaCategoriaNombre.value = '';
    }
    
    btnCerrarModal.addEventListener('click', cerrarModalNuevaCategoria);
    btnCancelarModal.addEventListener('click', cerrarModalNuevaCategoria);
    
    // Cerrar modal al hacer click fuera
    modal.addEventListener('click', function(e) {
        if (e.target === modal) {
            cerrarModalNuevaCategoria();
        }
    });
    
    // Confirmar creación de categoría
    btnConfirmarNuevaCategoria.addEventListener('click', function() {
        const nombreCategoria = nuevaCategoriaNombre.value.trim();
        
        if (nombreCategoria === '') {
            mostrarNotificacion('Por favor ingresa un nombre para la categoría', 'warning');
            return;
        }
        
        // Verificar si ya existe
        if (categorias.some(cat => cat.toLowerCase() === nombreCategoria.toLowerCase())) {
            mostrarNotificacion('Esta categoría ya existe', 'info');
            seleccionarCategoria(nombreCategoria);
            cerrarModalNuevaCategoria();
            return;
        }
        
        // Agregar nueva categoría
        const newOption = document.createElement('option');
        newOption.value = nombreCategoria;
        newOption.text = nombreCategoria;
        categoriaSelect.appendChild(newOption);
        
        categorias.push(nombreCategoria);
        
        // Seleccionar la nueva categoría
        seleccionarCategoria(nombreCategoria);
        
        cerrarModalNuevaCategoria();
        mostrarNotificacion(`Categoría "${nombreCategoria}" creada exitosamente`, 'success');
    });
    
    // Enter para confirmar en el modal
    nuevaCategoriaNombre.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            btnConfirmarNuevaCategoria.click();
        }
    });
    
    // ========== CONTROL DE STOCK ==========
    
    btnRestarStock.addEventListener('click', function() {
        let valor = parseInt(stockInput.value) || 0;
        if (valor > 0) {
            stockInput.value = valor - 1;
        }
    });
    
    btnSumarStock.addEventListener('click', function() {
        let valor = parseInt(stockInput.value) || 0;
        stockInput.value = valor + 1;
    });
    
    // ========== CONTADOR DE CARACTERES ==========
    
    const contadorCaracteres = document.getElementById('contadorCaracteres');
    
    descripcionTextarea.addEventListener('input', function() {
        const longitud = this.value.length;
        contadorCaracteres.textContent = `${longitud}/500`;
        
        if (longitud >= 10 && longitud <= 500) {
            this.classList.add('is-valid');
            this.classList.remove('is-invalid');
        } else {
            this.classList.add('is-invalid');
            this.classList.remove('is-valid');
        }
    });
    
    // Inicializar contador
    if (descripcionTextarea.value) {
        descripcionTextarea.dispatchEvent(new Event('input'));
    }
    
    // ========== DRAG & DROP DE IMÁGENES ==========
    
    dropZone.addEventListener('click', function() {
        imagenInput.click();
    });
    
    imagenInput.addEventListener('change', function() {
        if (this.files && this.files[0]) {
            mostrarVistaPrevia(this.files[0]);
        }
    });
    
    // Drag & Drop
    dropZone.addEventListener('dragover', function(e) {
        e.preventDefault();
        this.classList.add('drag-over');
    });
    
    dropZone.addEventListener('dragleave', function(e) {
        e.preventDefault();
        this.classList.remove('drag-over');
    });
    
    dropZone.addEventListener('drop', function(e) {
        e.preventDefault();
        this.classList.remove('drag-over');
        
        const files = e.dataTransfer.files;
        if (files.length > 0) {
            const file = files[0];
            
            // Validar que sea imagen
            if (file.type.startsWith('image/')) {
                // Validar tamaño (5MB)
                if (file.size > 5 * 1024 * 1024) {
                    mostrarNotificacion('La imagen no debe superar 5MB', 'warning');
                    return;
                }
                
                imagenInput.files = files;
                mostrarVistaPrevia(file);
            } else {
                mostrarNotificacion('Por favor selecciona un archivo de imagen', 'warning');
            }
        }
    });
    
    function mostrarVistaPrevia(file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            imagenPreviewImg.src = e.target.result;
            imagenPlaceholder.style.display = 'none';
            imagenPreview.style.display = 'block';
        };
        reader.readAsDataURL(file);
    }
    
    btnEliminarImagen.addEventListener('click', function(e) {
        e.stopPropagation();
        imagenInput.value = '';
        imagenPreview.style.display = 'none';
        imagenPlaceholder.style.display = 'block';
    });
    
    // ========== ESTADO DEL PRODUCTO ==========
    
    const estadoTexto = document.getElementById('estadoTexto');
    
    disponibleCheckbox.addEventListener('change', function() {
        if (this.checked) {
            estadoTexto.textContent = 'Disponible para ordenar';
            estadoTexto.className = 'badge bg-success';
        } else {
            estadoTexto.textContent = 'No disponible';
            estadoTexto.className = 'badge bg-secondary';
        }
    });
    
    // ========== VISTA PREVIA EN TIEMPO REAL ==========
    
    nombreInput.addEventListener('input', function() {
        document.getElementById('previewNombre').textContent = this.value || 'Nombre del Producto';
    });
    
    precioInput.addEventListener('input', function() {
        const precio = parseFloat(this.value) || 0;
        document.getElementById('previewPrecio').textContent = `S/ ${precio.toFixed(2)}`;
        document.getElementById('previewPrecioDetalle').textContent = `S/ ${precio.toFixed(2)}`;
    });
    
    descripcionTextarea.addEventListener('input', function() {
        document.getElementById('previewDescripcion').textContent = this.value || 'Descripción breve del producto...';
    });
    
    stockInput.addEventListener('input', function() {
        const stock = parseInt(this.value) || 0;
        document.getElementById('previewStock').textContent = `${stock} unidades`;
    });
    
    disponibleCheckbox.addEventListener('change', function() {
        const previewDisponibilidad = document.getElementById('previewDisponibilidad');
        if (this.checked) {
            previewDisponibilidad.innerHTML = '<i class="bi bi-check-circle-fill"></i> Disponible';
            previewDisponibilidad.className = 'badge position-absolute top-0 end-0 m-3 fs-6 disponible-badge';
        } else {
            previewDisponibilidad.innerHTML = '<i class="bi bi-x-circle-fill"></i> No Disponible';
            previewDisponibilidad.className = 'badge position-absolute top-0 end-0 m-3 fs-6 no-disponible-badge';
        }
    });
    
    // Actualizar categoría en preview cuando se selecciona
    const originalSeleccionarCategoria = seleccionarCategoria;
    seleccionarCategoria = function(nombreCategoria) {
        originalSeleccionarCategoria(nombreCategoria);
        document.getElementById('previewCategoria').innerHTML = `<i class="bi bi-bookmark-fill"></i> ${nombreCategoria}`;
        document.getElementById('previewCategoriaDetalle').textContent = nombreCategoria;
    };
    
    // Actualizar imagen en preview
    imagenInput.addEventListener('change', function() {
        if (this.files && this.files[0]) {
            const reader = new FileReader();
            reader.onload = function(e) {
                document.getElementById('previewImagen').src = e.target.result;
            };
            reader.readAsDataURL(this.files[0]);
        }
    });
    
    // ========== LIMPIAR FORMULARIO ==========
    
    document.getElementById('btnLimpiarForm').addEventListener('click', function() {
        setTimeout(() => {
            categoriaSeleccionada.style.display = 'none';
            imagenPreview.style.display = 'none';
            imagenPlaceholder.style.display = 'block';
            contadorCaracteres.textContent = '0/500';
            
            // Resetear preview
            document.getElementById('previewNombre').textContent = 'Nombre del Producto';
            document.getElementById('previewPrecio').textContent = 'S/ 0.00';
            document.getElementById('previewPrecioDetalle').textContent = 'S/ 0.00';
            document.getElementById('previewDescripcion').textContent = 'Descripción breve del producto...';
            document.getElementById('previewStock').textContent = '0 unidades';
            document.getElementById('previewCategoria').innerHTML = '<i class="bi bi-bookmark-fill"></i> Categoría';
            document.getElementById('previewCategoriaDetalle').textContent = 'Sin categoría';
            document.getElementById('previewDisponibilidad').innerHTML = '<i class="bi bi-check-circle-fill"></i> Disponible';
            document.getElementById('previewDisponibilidad').className = 'badge position-absolute top-0 end-0 m-3 fs-6 disponible-badge';
            document.getElementById('previewImagen').src = 'https://via.placeholder.com/400x300/e9ecef/6c757d?text=Sin+Imagen';
        }, 10);
    });
    
    // ========== VALIDACIÓN DEL FORMULARIO ==========
    
    productoForm.addEventListener('submit', function(e) {
        let valido = true;
        
        // Validar categoría manualmente (ya que quitamos required del select oculto)
        if (categoriaSelect.value === '') {
            e.preventDefault();
            categoriaSelect.classList.add('is-invalid');
            categoriaSearch.classList.add('is-invalid');
            mostrarNotificacion('Por favor selecciona una categoría', 'warning');
            
            // Enfocar el campo de búsqueda
            categoriaSearch.focus();
            valido = false;
        } else {
            categoriaSelect.classList.remove('is-invalid');
            categoriaSearch.classList.remove('is-invalid');
        }
        
        if (valido) {
            // Mostrar loading en el botón
            const btnSubmit = this.querySelector('button[type="submit"]');
            btnSubmit.classList.add('loading');
            btnSubmit.disabled = true;
        }
    });
    
    // ========== NOTIFICACIONES ==========
    
    function mostrarNotificacion(mensaje, tipo = 'info') {
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${tipo} alert-dismissible fade show position-fixed`;
        alertDiv.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px; box-shadow: 0 0.5rem 1rem rgba(0,0,0,0.15);';
        alertDiv.innerHTML = `
            <div class="d-flex align-items-center">
                <i class="bi bi-${tipo === 'success' ? 'check-circle' : tipo === 'warning' ? 'exclamation-triangle' : 'info-circle'} me-2"></i>
                <div>${mensaje}</div>
            </div>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        
        document.body.appendChild(alertDiv);
        
        setTimeout(() => {
            alertDiv.classList.remove('show');
            setTimeout(() => alertDiv.remove(), 150);
        }, 3000);
    }
    
    // ========== INICIALIZACIÓN DE IMAGEN PLACEHOLDER ==========
    
    // Crear imagen SVG por defecto para preview
    const defaultImageSVG = 'data:image/svg+xml,%3Csvg xmlns=\'http://www.w3.org/2000/svg\' width=\'400\' height=\'300\'%3E%3Crect width=\'400\' height=\'300\' fill=\'%23e9ecef\'/%3E%3Ctext x=\'50%25\' y=\'50%25\' dominant-baseline=\'middle\' text-anchor=\'middle\' font-family=\'Arial, sans-serif\' font-size=\'20\' fill=\'%236c757d\'%3ESin Imagen%3C/text%3E%3C/svg%3E';
    
    // Establecer imagen por defecto si no hay imagen
    if (imagenPreviewImg && !imagenPreviewImg.src) {
        imagenPreviewImg.src = defaultImageSVG;
    }
    
    // Manejar error de carga de imagen
    if (imagenPreviewImg) {
        imagenPreviewImg.addEventListener('error', function() {
            this.src = defaultImageSVG;
        });
    }
});
