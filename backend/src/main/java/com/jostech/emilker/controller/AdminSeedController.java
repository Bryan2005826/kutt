package com.jostech.emilker.controller;

import com.jostech.emilker.model.Negocio;
import com.jostech.emilker.model.Usuario;
import com.jostech.emilker.repository.AdicionalRepository;
import com.jostech.emilker.repository.BarberoRepository;
import com.jostech.emilker.repository.CitaRepository;
import com.jostech.emilker.repository.FavoritoRepository;
import com.jostech.emilker.repository.MetodoPagoRepository;
import com.jostech.emilker.repository.NegocioRepository;
import com.jostech.emilker.repository.ProductoRepository;
import com.jostech.emilker.repository.ResenaRepository;
import com.jostech.emilker.repository.UsuarioRepository;
import com.jostech.emilker.repository.VentaRepository;
import com.jostech.emilker.util.PasswordPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Endpoint temporal para limpiar los negocios de demostracion y cargar negocios reales
// SIN escribir sus correos ni contrasenas en el codigo: los datos viajan en el cuerpo de la
// peticion (los envia el script cargar-negocios.ps1 desde tu PC). Es seguro llamarlo mas de
// una vez: si un negocio ya existe (mismo correo de administrador) se actualiza en vez de
// duplicarse. Protegido con una clave (variable de entorno SEED_KEY); si esa variable no esta
// configurada, el endpoint queda deshabilitado (responde 404).
@RestController
@RequestMapping("/api/admin")
public class AdminSeedController {

    // Correos de administrador de los 10 negocios ficticios que sembraba el DataSeeder.
    private static final List<String> CORREOS_DEMO = List.of(
            "emilker@barbershop.com", "contacto@nailsstudio.com", "contacto@glowestetica.com",
            "elrey@barbershop.com", "unasydetalles@correo.com", "oasis@spa.com", "zen@spa.com",
            "bambu@spa.com", "pielperfecta@correo.com", "renova@correo.com");

    public record NegocioReal(String nombre, String correo, String password, String negocio, String rubro,
                              String telefono, String whatsapp, String departamento, String direccion,
                              String logoUrl, String portadaUrl, Double latitud, Double longitud) {}

    public record CargaRequest(Boolean eliminarDemo, List<NegocioReal> negocios) {}

    private final NegocioRepository negocioRepository;
    private final UsuarioRepository usuarioRepository;
    private final CitaRepository citaRepository;
    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final BarberoRepository barberoRepository;
    private final AdicionalRepository adicionalRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final ResenaRepository resenaRepository;
    private final FavoritoRepository favoritoRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.seed-key:}")
    private String seedKey;

    public AdminSeedController(NegocioRepository negocioRepository, UsuarioRepository usuarioRepository,
                               CitaRepository citaRepository, VentaRepository ventaRepository,
                               ProductoRepository productoRepository, BarberoRepository barberoRepository,
                               AdicionalRepository adicionalRepository, MetodoPagoRepository metodoPagoRepository,
                               ResenaRepository resenaRepository, FavoritoRepository favoritoRepository,
                               PasswordEncoder passwordEncoder) {
        this.negocioRepository = negocioRepository;
        this.usuarioRepository = usuarioRepository;
        this.citaRepository = citaRepository;
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.barberoRepository = barberoRepository;
        this.adicionalRepository = adicionalRepository;
        this.metodoPagoRepository = metodoPagoRepository;
        this.resenaRepository = resenaRepository;
        this.favoritoRepository = favoritoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/cargar-negocios")
    @Transactional
    public ResponseEntity<?> cargarNegocios(@RequestHeader(value = "X-Seed-Key", required = false) String clave,
                                            @RequestBody CargaRequest request) {
        if (seedKey == null || seedKey.isBlank()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!seedKey.equals(clave)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Clave inválida.");
        }

        // Validamos todo ANTES de borrar nada, para no dejar la base a medias.
        List<NegocioReal> negocios = request.negocios() == null ? List.of() : request.negocios();
        for (NegocioReal r : negocios) {
            if (r.correo() == null || r.correo().isBlank() || r.negocio() == null || r.negocio().isBlank()) {
                return ResponseEntity.badRequest().body("Cada negocio necesita correo y nombre del negocio.");
            }
            String errorPassword = PasswordPolicy.mensajeDeError(r.password());
            if (errorPassword != null) {
                return ResponseEntity.badRequest().body(r.correo() + ": " + errorPassword);
            }
        }

        int eliminados = 0;
        if (Boolean.TRUE.equals(request.eliminarDemo())) {
            for (String correo : CORREOS_DEMO) {
                Negocio n = negocioRepository.findByAdminCorreo(correo).orElse(null);
                if (n != null) {
                    eliminarNegocio(n);
                    eliminados++;
                }
                Usuario u = usuarioRepository.findByCorreo(correo).orElse(null);
                if (u != null && "ADMIN_NEGOCIO".equals(u.getRol())) {
                    usuarioRepository.delete(u);
                }
            }
        }

        int creados = 0;
        int actualizados = 0;
        for (NegocioReal r : negocios) {
            String correo = r.correo().trim().toLowerCase();

            Usuario usuario = usuarioRepository.findByCorreo(correo).orElseGet(Usuario::new);
            usuario.setCorreo(correo);
            usuario.setNombre(r.nombre() == null || r.nombre().isBlank() ? r.negocio() : r.nombre().trim());
            usuario.setPasswordHash(passwordEncoder.encode(r.password()));
            usuario.setRol("ADMIN_NEGOCIO");
            usuarioRepository.save(usuario);

            Negocio n = negocioRepository.findByAdminCorreo(correo).orElse(null);
            if (n == null) {
                n = new Negocio();
                creados++;
            } else {
                actualizados++;
            }
            n.setAdminCorreo(correo);
            n.setNombre(r.negocio().trim());
            n.setRubro(r.rubro() == null || r.rubro().isBlank() ? "OTRO" : r.rubro().trim().toUpperCase());
            n.setTelefonoFijo(r.telefono() == null ? "" : r.telefono().trim());
            n.setWhatsapp(r.whatsapp() == null ? "" : r.whatsapp().trim());
            n.setDepartamento(r.departamento() == null ? "" : r.departamento().trim());
            n.setDireccion(r.direccion() == null ? "" : r.direccion().trim());
            n.setLogoUrl(r.logoUrl());
            n.setPortadaUrl(r.portadaUrl());
            n.setLatitud(r.latitud());
            n.setLongitud(r.longitud());
            n.setVerificado(true);
            negocioRepository.save(n);
        }

        return ResponseEntity.ok("Negocios demo eliminados: " + eliminados
                + " | Negocios reales creados: " + creados + " | actualizados: " + actualizados);
    }

    // Borra un negocio y todo lo que colgaba de el (no hay llaves foraneas en la base, asi
    // que hay que limpiar cada tabla a mano para no dejar filas huerfanas).
    private void eliminarNegocio(Negocio n) {
        Long id = n.getId();
        citaRepository.deleteAll(citaRepository.findByNegocioId(id));
        ventaRepository.deleteAll(ventaRepository.findByNegocioId(id));
        productoRepository.deleteAll(productoRepository.findByNegocioId(id));
        barberoRepository.deleteAll(barberoRepository.findByNegocioId(id));
        adicionalRepository.deleteAll(adicionalRepository.findByNegocioId(id));
        metodoPagoRepository.deleteAll(metodoPagoRepository.findByNegocioId(id));
        resenaRepository.deleteAll(resenaRepository.findByNegocioId(id));
        favoritoRepository.deleteAll(favoritoRepository.findAll().stream()
                .filter(f -> id.equals(f.getNegocioId())).toList());
        negocioRepository.delete(n);
    }
}
