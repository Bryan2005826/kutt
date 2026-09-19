package com.jostech.emilker.controller;

import com.jostech.emilker.dto.AuthResponse;
import com.jostech.emilker.dto.LoginRequest;
import com.jostech.emilker.dto.RegistroRequest;
import com.jostech.emilker.model.Cliente;
import com.jostech.emilker.model.Negocio;
import com.jostech.emilker.model.Usuario;
import com.jostech.emilker.repository.ClienteRepository;
import com.jostech.emilker.repository.NegocioRepository;
import com.jostech.emilker.repository.UsuarioRepository;
import com.jostech.emilker.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final NegocioRepository negocioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UsuarioRepository usuarioRepository, ClienteRepository clienteRepository,
                           NegocioRepository negocioRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.negocioRepository = negocioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // CU-02: registro de cuenta (Cliente o Admin de un negocio)
    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody RegistroRequest request) {
        if (usuarioRepository.findByCorreo(request.getCorreo()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ese correo ya está registrado.");
        }

        String rol = request.getRol() != null && !request.getRol().isBlank() ? request.getRol() : "CLIENTE";

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setCorreo(request.getCorreo());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(rol);
        usuarioRepository.save(usuario);

        if ("ADMIN_NEGOCIO".equals(rol)) {
            Negocio negocio = new Negocio();
            negocio.setNombre(request.getNombreNegocio());
            negocio.setRubro(request.getRubro());
            negocio.setTelefonoFijo(request.getTelefonoFijoNegocio());
            negocio.setDepartamento(request.getDepartamentoNegocio());
            negocio.setDireccion(request.getDireccionNegocio());
            negocio.setLogoUrl(request.getLogoUrl());
            negocio.setPortadaUrl(request.getPortadaUrl());
            negocio.setAdminCorreo(request.getCorreo());
            negocio.setVerificado(false);
            negocioRepository.save(negocio);
        } else {
            Cliente cliente = clienteRepository.findByCorreo(request.getCorreo()).orElseGet(Cliente::new);
            cliente.setNombre(request.getNombre());
            cliente.setApellidos(request.getApellidos());
            cliente.setCorreo(request.getCorreo());
            cliente.setTelefono(request.getTelefono());
            cliente.setDepartamento(request.getDepartamento());
            cliente.setCiudad(request.getCiudad());
            cliente.setGenero(request.getGenero());
            cliente.setFechaNacimiento(request.getFechaNacimiento());
            cliente.setDireccion(request.getDireccion());
            cliente.setPermiteUbicacion(request.isPermiteUbicacion());
            clienteRepository.save(cliente);
        }

        String token = jwtUtil.generarToken(usuario.getCorreo(), usuario.getRol());
        return ResponseEntity.ok(new AuthResponse(token, usuario.getNombre(), usuario.getCorreo(), usuario.getRol()));
    }

    // CU-01: iniciar sesión
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo()).orElse(null);

        if (usuario == null || !passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Correo o contraseña incorrectos.");
        }

        String token = jwtUtil.generarToken(usuario.getCorreo(), usuario.getRol());
        return ResponseEntity.ok(new AuthResponse(token, usuario.getNombre(), usuario.getCorreo(), usuario.getRol()));
    }
}
