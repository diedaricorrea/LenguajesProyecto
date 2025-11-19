package com.example.Ejemplo.services.impl;

import com.example.Ejemplo.dto.PedidoDTO;
import com.example.Ejemplo.mapper.PedidoMapper;
import com.example.Ejemplo.models.*;
import com.example.Ejemplo.repository.PedidosRepository;
import com.example.Ejemplo.services.NotificacionService;
import com.example.Ejemplo.services.PedidosService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class PedidosServiceImpl implements PedidosService {

    private static final String LETRAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final Set<String> codigosGenerados = new HashSet<>();
    private final Random random = new Random();

    private final PedidosRepository pedidosRepository;
    private final PedidoMapper pedidoMapper;
    private final NotificacionService notificacionService;

    public PedidosServiceImpl(PedidosRepository pedidosRepository,
                             PedidoMapper pedidoMapper,
                             NotificacionService notificacionService) {
        this.pedidosRepository = pedidosRepository;
        this.pedidoMapper = pedidoMapper;
        this.notificacionService = notificacionService;
    }

    // ==================== Métodos CRUD básicos ====================
    
    @Override
    @Transactional
    public Pedido guardarPedido(Pedido pedido) {
        log.debug("Guardando pedido: {}", pedido.getCodigoPedido());
        return pedidosRepository.save(pedido);
    }

    @Override
    @Transactional
    public int eliminarPedido(String codigoPedido) {
        log.debug("Eliminando pedido: {}", codigoPedido);
        return pedidosRepository.deleteByCodigoPedido(codigoPedido);
    }
    
    // ==================== Métodos de consulta ====================

    @Override
    public List<Pedido> obtenerTodosPedidos() {
        return pedidosRepository.findAll();
    }

    @Override
    public List<Pedido> obtenerPedidosPorUsuario(int idUsuario) {
        return pedidosRepository.findAllByUsuario_IdUsuario(idUsuario);
    }
    
    @Override
    public Optional<Pedido> buscarPorId(Integer id) {
        return pedidosRepository.findById(id);
    }
    
    @Override
    public List<Pedido> obtenerPedidosPorEstado(EstadoPedido estado) {
        return pedidosRepository.findAllByEstado(estado);
    }
    
    @Override
    public List<Pedido> buscarPorCodigoPedido(String codigoPedido) {
        return pedidosRepository.findByCodigoPedido(codigoPedido);
    }
    
    // ==================== Métodos de agrupación y conversión ====================
    
    @Override
    public Map<EstadoPedido, List<PedidoDTO>> agruparPedidosPorEstado() {
        List<Pedido> pedidos = obtenerTodosPedidos();
        List<PedidoDTO> pedidosDTO = pedidoMapper.toDTOList(pedidos);
        
        return pedidosDTO.stream()
                .collect(Collectors.groupingBy(
                        PedidoDTO::getEstado,
                        () -> new EnumMap<>(EstadoPedido.class),
                        Collectors.toList()
                ));
    }
    
    @Override
    public Map<EstadoPedido, List<PedidoDTO>> agruparPedidosPorEstadoYFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);
        
        List<Pedido> pedidos = pedidosRepository.findByFechaPedidoBetween(inicio, fin);
        List<PedidoDTO> pedidosDTO = pedidoMapper.toDTOList(pedidos);
        
        return pedidosDTO.stream()
                .collect(Collectors.groupingBy(
                        PedidoDTO::getEstado,
                        () -> new EnumMap<>(EstadoPedido.class),
                        Collectors.toList()
                ));
    }
    
    @Override
    public List<PedidoDTO> convertirPedidosADTO(List<Pedido> pedidos) {
        return pedidoMapper.toDTOList(pedidos);
    }
    
    // ==================== Métodos de gestión de estados ====================
    
    @Override
    @Transactional
    public boolean avanzarEstadoPedido(Integer idPedido, Integer idUsuarioNotificar) {
        try {
            Pedido pedido = buscarPorId(idPedido)
                    .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
            
            EstadoPedido estadoAnterior = pedido.getEstado();
            
            if (pedido.avanzarEstado()) {
                guardarPedido(pedido);
                
                // Crear notificación según el nuevo estado
                String mensaje = generarMensajeNotificacion(pedido);
                if (mensaje != null && idUsuarioNotificar != null) {
                    notificacionService.crearNotificacion(idUsuarioNotificar, mensaje);
                }
                
                log.info("Pedido {} avanzado de {} a {}", 
                        pedido.getCodigoPedido(), estadoAnterior, pedido.getEstado());
                
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            log.error("Error al avanzar estado del pedido: {}", idPedido, e);
            throw new RuntimeException("Error al actualizar el pedido: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean cancelarPedido(Integer idPedido, String motivo, Integer idUsuarioNotificar) {
        try {
            Pedido pedido = buscarPorId(idPedido)
                    .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
            
            if (pedido.cancelar()) {
                guardarPedido(pedido);
                
                // Notificar al usuario
                if (idUsuarioNotificar != null) {
                    String mensaje = String.format(
                        "Tu pedido %s ha sido cancelado.%s",
                        pedido.getCodigoPedido(),
                        motivo != null && !motivo.trim().isEmpty() ? " Motivo: " + motivo : ""
                    );
                    
                    notificacionService.crearNotificacion(idUsuarioNotificar, mensaje);
                }
                
                log.info("Pedido {} cancelado. Motivo: {}", pedido.getCodigoPedido(), motivo);
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            log.error("Error al cancelar pedido: {}", idPedido, e);
            throw new RuntimeException("Error al cancelar el pedido: " + e.getMessage());
        }
    }
    
    /**
     * Genera el mensaje de notificación según el estado del pedido
     */
    private String generarMensajeNotificacion(Pedido pedido) {
        switch (pedido.getEstado()) {
            case EN_PREPARACION:
                return String.format("Tu pedido %s está siendo preparado 🍳", 
                                   pedido.getCodigoPedido());
            case LISTO:
                return String.format("¡Tu pedido %s está listo! Puedes pasar a recogerlo 🎉", 
                                   pedido.getCodigoPedido());
            case ENTREGADO:
                return String.format("Pedido %s entregado. ¡Gracias por tu compra! 😊", 
                                   pedido.getCodigoPedido());
            default:
                return null;
        }
    }
    
    // ==================== Utilidades ====================

    @Override
    public String generarCodigoUnico() {
        int intentos = 0;
        int MAX_INTENTOS = 1000;

        while (intentos < MAX_INTENTOS) {
            String codigo = generarCodigoAleatorio();

            if (!codigosGenerados.contains(codigo)) {
                codigosGenerados.add(codigo);
                return codigo;
            }

            intentos++;
        }

        throw new RuntimeException("No se pudo generar un código único tras varios intentos");
    }

    public List<String> obtenerTodosLosCodigos() {
        return new ArrayList<>(codigosGenerados);
    }

    private String generarCodigoAleatorio() {
        StringBuilder codigo = new StringBuilder();

        for (int i = 0; i < 3; i++) {
            codigo.append(LETRAS.charAt(random.nextInt(LETRAS.length())));
        }

        for (int i = 0; i < 3; i++) {
            codigo.append(random.nextInt(10));
        }

        return codigo.toString();
    }
}
