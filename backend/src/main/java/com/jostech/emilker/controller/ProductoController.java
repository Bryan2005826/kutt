package com.jostech.emilker.controller;

import com.jostech.emilker.model.Negocio;
import com.jostech.emilker.model.Producto;
import com.jostech.emilker.repository.NegocioRepository;
import com.jostech.emilker.repository.ProductoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoRepository productoRepository;
    private final NegocioRepository negocioRepository;

    public ProductoController(ProductoRepository productoRepository, NegocioRepository negocioRepository) {
        this.productoRepository = productoRepository;
        this.negocioRepository = negocioRepository;
    }

    @GetMapping
    public List<Producto> listar(@RequestParam(required = false) Long negocioId, Authentication auth) {
        if (negocioId != null) return productoRepository.findByNegocioId(negocioId);
        Negocio miNegocio = negocioDelAdmin(auth);
        return miNegocio != null ? productoRepository.findByNegocioId(miNegocio.getId()) : List.of();
    }

    @PostMapping
    public Producto crear(@RequestBody Producto producto, Authentication auth) {
        Negocio miNegocio = negocioDelAdmin(auth);
        if (miNegocio == null) throw new IllegalArgumentException("No se encontró un negocio para esta cuenta.");
        producto.setNegocioId(miNegocio.getId());
        return productoRepository.save(producto);
    }

    // El administrador edita un producto ya existente: precio, foto, o la cantidad
    // (por ejemplo cuando le llega mercancia nueva y necesita actualizar el stock).
    @PutMapping("/{id}")
    public Producto editar(@PathVariable Long id, @RequestBody Producto datos) {
        Producto producto = productoRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Producto no encontrado: " + id));
        producto.setNombre(datos.getNombre());
        producto.setPrecio(datos.getPrecio());
        producto.setExistencias(datos.getExistencias());
        producto.setMinimo(datos.getMinimo());
        producto.setFotoUrl(datos.getFotoUrl());
        return productoRepository.save(producto);
    }

    // El administrador elimina un producto que ya no vende / ya no existe en el
    // inventario. Verificamos que el producto sea de SU negocio antes de borrar,
    // para que un admin no pueda borrar productos de otro negocio solo adivinando el id.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, Authentication auth) {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto == null) {
            return ResponseEntity.notFound().build();
        }
        Negocio miNegocio = negocioDelAdmin(auth);
        if (miNegocio == null || !miNegocio.getId().equals(producto.getNegocioId())) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN)
                    .body("No puedes eliminar un producto que no pertenece a tu negocio.");
        }
        productoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Negocio negocioDelAdmin(Authentication auth) {
        if (auth == null) return null;
        return negocioRepository.findByAdminCorreo(auth.getName()).orElse(null);
    }
}
