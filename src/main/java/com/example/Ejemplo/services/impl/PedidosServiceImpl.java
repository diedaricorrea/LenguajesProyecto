package com.example.Ejemplo.services.impl;

import com.example.Ejemplo.models.*;
import com.example.Ejemplo.repository.PedidosRepository;
import com.example.Ejemplo.services.PedidosService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PedidosServiceImpl implements PedidosService {

    private static final String LETRAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final Set<String> codigosGenerados = new HashSet<>();
    private final Random random = new Random();

    private final PedidosRepository pedidosRepository;

    public PedidosServiceImpl(PedidosRepository pedidosRepository) {
        this.pedidosRepository = pedidosRepository;
    }

    @Override
    public Pedido guardarPedido(Pedido pedido) {
        return pedidosRepository.save(pedido);
    }

    @Override
    public List<Pedido> findAllPedidos() {
        return pedidosRepository.findAllByEstado(false);
    }

    @Override
    public List<Pedido> findByUsuario_Id(int id){
        return pedidosRepository.findAllByUsuario_IdUsuario(id);
    }

    @Override
    public int deletePedido(String codigoPedido) {
        return pedidosRepository.deleteByCodigoPedido(codigoPedido);
    }

    public List<PedidoResumenDTO> obtenerDetallePedidos() {
        List<Pedido> pedidos = pedidosRepository.findAllByEstado(false);

        return getPedidoResumenDTOS(pedidos);
    }

    public List<PedidoResumenDTO> obtenerDetallePedidosEnviados() {
        List<Pedido> pedidos = pedidosRepository.findAllByEstado(true);

        return getPedidoResumenDTOS(pedidos);
    }

    public List<PedidoResumenDTO> obtenerDetallePedidosPorId(int id) {
        List<Pedido> pedidos = pedidosRepository.findAllByUsuario_IdUsuario(id);

        return getPedidoResumenDTOS(pedidos);
    }

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

    public List<PedidoResumenDTO> buscarPorCodigoPedido(String codigo) {
        List<Pedido> pedidos = pedidosRepository.findByCodigoPedido(codigo);
        return getPedidoResumenDTOS(pedidos) ;
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

    private List<PedidoResumenDTO> getPedidoResumenDTOS(List<Pedido> pedidos) {
        return pedidos.stream().map(pedido -> {
            String nombreUsuario = pedido.getUsuario().getNombre();
            int idUsuario = pedido.getUsuario().getIdUsuario();
            LocalTime fechaEntrega = pedido.getFechaEntrega();
            boolean estado = pedido.isEstado();
            String codigoPedido = pedido.getCodigoPedido();
            List<DetallePedido> productos =  pedidos.stream()
                    .filter(pe -> pe.getCodigoPedido().equals(codigoPedido))
                    .flatMap(pe-> pe.getDetallePedido().stream()) // "Aplana" todas las listas
                    .collect(Collectors.toList());

            return new PedidoResumenDTO(idUsuario,codigoPedido,nombreUsuario, productos, fechaEntrega,estado);
        }).collect(Collectors.toList());
    }
}
