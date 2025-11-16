package com.example.Ejemplo.models;

/**
 * Estados del ciclo de vida de un pedido en la cafetería
 * 
 * Flujo normal:
 * PENDIENTE → EN_PREPARACION → LISTO → ENTREGADO
 * 
 * Estados especiales:
 * CANCELADO (puede ocurrir desde PENDIENTE o EN_PREPARACION)
 */
public enum EstadoPedido {
    /**
     * Pedido recién creado, esperando ser procesado por la cafetería
     */
    PENDIENTE("Pendiente", "warning", "bi-clock-history"),
    
    /**
     * Pedido en proceso de preparación
     */
    EN_PREPARACION("En Preparación", "info", "bi-hourglass-split"),
    
    /**
     * Pedido terminado, esperando que el cliente lo recoja
     */
    LISTO("Listo para Recoger", "success", "bi-check-circle"),
    
    /**
     * Pedido entregado al cliente
     */
    ENTREGADO("Entregado", "secondary", "bi-bag-check"),
    
    /**
     * Pedido cancelado (por usuario o administrador)
     */
    CANCELADO("Cancelado", "danger", "bi-x-circle");
    
    private final String displayName;
    private final String bootstrapClass; // warning, info, success, danger, secondary
    private final String iconClass; // Bootstrap Icons
    
    EstadoPedido(String displayName, String bootstrapClass, String iconClass) {
        this.displayName = displayName;
        this.bootstrapClass = bootstrapClass;
        this.iconClass = iconClass;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getBootstrapClass() {
        return bootstrapClass;
    }
    
    public String getIconClass() {
        return iconClass;
    }
    
    /**
     * Valida si es posible transicionar al estado destino
     */
    public boolean puedeTransicionarA(EstadoPedido destino) {
        switch (this) {
            case PENDIENTE:
                return destino == EN_PREPARACION || destino == CANCELADO;
            case EN_PREPARACION:
                return destino == LISTO || destino == CANCELADO;
            case LISTO:
                return destino == ENTREGADO;
            case ENTREGADO:
            case CANCELADO:
                return false; // Estados finales
            default:
                return false;
        }
    }
    
    /**
     * Obtiene el siguiente estado en el flujo normal
     */
    public EstadoPedido getSiguienteEstado() {
        switch (this) {
            case PENDIENTE:
                return EN_PREPARACION;
            case EN_PREPARACION:
                return LISTO;
            case LISTO:
                return ENTREGADO;
            default:
                return this; // Estados finales no tienen siguiente
        }
    }
    
    /**
     * Verifica si el estado es final (no puede cambiar)
     */
    public boolean esFinal() {
        return this == ENTREGADO || this == CANCELADO;
    }
}
