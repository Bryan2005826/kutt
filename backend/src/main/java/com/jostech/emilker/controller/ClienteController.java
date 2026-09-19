package com.jostech.emilker.controller;

import com.jostech.emilker.model.Cliente;
import com.jostech.emilker.repository.ClienteRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteRepository clienteRepository;

    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @GetMapping
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    // Usado al iniciar sesión: si el correo no existe, crea el cliente; si existe, lo retorna.
    @PostMapping("/asegurar")
    public Cliente asegurar(@RequestBody Cliente datos) {
        return clienteRepository.findByCorreo(datos.getCorreo())
                .orElseGet(() -> {
                    Cliente nuevo = new Cliente();
                    nuevo.setNombre(datos.getNombre());
                    nuevo.setCorreo(datos.getCorreo());
                    nuevo.setTelefono(datos.getTelefono() != null ? datos.getTelefono() : "No registrado");
                    return clienteRepository.save(nuevo);
                });
    }

    // CU-05: editar información del cliente
    @PutMapping("/{id}")
    public Cliente editar(@PathVariable Long id, @RequestBody Cliente datos) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Cliente no encontrado: " + id));
        cliente.setNombre(datos.getNombre());
        cliente.setTelefono(datos.getTelefono());
        return clienteRepository.save(cliente);
    }
}
