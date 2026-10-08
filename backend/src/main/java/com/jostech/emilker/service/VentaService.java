package com.jostech.emilker.service;

import com.jostech.emilker.dto.CompraRequest;
import com.jostech.emilker.model.MetodoPago;
import com.jostech.emilker.model.Producto;
import com.jostech.emilker.model.Venta;
import com.jostech.emilker.repository.MetodoPagoRepository;
import com.jostech.emilker.repository.ProductoRepository;
import com.jostech.emilker.repository.VentaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final MetodoPagoRepository metodoPagoRepository;

    public VentaService(VentaRepository ventaRepository, ProductoRepository productoRepository,
                         MetodoPagoRepository metodoPagoRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.metodoPagoRepository = metodoPagoRepository;
    }

    // CU-07: compra de productos independiente de una cita, dentro de un negocio concreto
    public Venta comprar(CompraRequest request) {
        if (request.getNegocioId() == null) {
            throw new IllegalArgumentException("Falta indicar a que negocio pertenece la compra.");
        }

        List<String> nombres = new ArrayList<>();
        double total = 0;

        for (CompraRequest.ItemCompra item : request.getItems()) {
            Producto producto = productoRepository.findById(item.getProductoId()).orElseThrow(() ->
                    new IllegalArgumentException("Producto no encontrado: " + item.getProductoId()));
            if (producto.getExistencias() < item.getCantidad()) {
                throw new IllegalArgumentException("Existencias insuficientes de: " + producto.getNombre());
            }
            producto.setExistencias(producto.getExistencias() - item.getCantidad());
            productoRepository.save(producto);

            nombres.add(item.getCantidad() + "x " + producto.getNombre());
            total += producto.getPrecio() * item.getCantidad();
        }

        Venta venta = new Venta();
        venta.setNegocioId(request.getNegocioId());
        venta.setCliente(request.getCliente());
        venta.setDetalle(String.join(", ", nombres));
        venta.setTotal(total);
        venta.setEstadoPago("Pendiente");

        return ventaRepository.save(venta);
    }

    // CU-08: generar el pago de una compra de productos
    public Venta pagar(Long id, Long metodoPagoId) {
        Venta venta = ventaRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Venta no encontrada: " + id));
        MetodoPago metodo = metodoPagoRepository.findById(metodoPagoId).orElseThrow(() ->
                new IllegalArgumentException("Metodo de pago no encontrado: " + metodoPagoId));

        venta.setMetodoPago(metodo.getNombre());
        boolean esDigital = "DIGITAL".equalsIgnoreCase(metodo.getTipo());
        venta.setTipoQr(esDigital ? "DIGITAL" : "TICKET");
        venta.setEstadoPago(esDigital ? "Pagado" : "Pendiente");

        return ventaRepository.save(venta);
    }
}
