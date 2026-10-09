package com.jostech.emilker.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.jostech.emilker.dto.AuthResponse;
import com.jostech.emilker.dto.FirebaseLoginRequest;
import com.jostech.emilker.dto.LoginRequest;
import com.jostech.emilker.dto.RegistroRequest;
import com.jostech.emilker.model.Cliente;
import com.jostech.emilker.model.Negocio;
import com.jostech.emilker.model.Usuario;
import com.jostech.emilker.repository.ClienteRepository;
import com.jostech.emilker.repository.NegocioRepository;
import com.jostech.emilker.repository.UsuarioRepository;
import com.jostech.emilker.security.JwtUtil;
import com.jostech.emilker.service.RecaptchaService;
import com.jostech.emilker.util.PasswordPolicy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthController.class);

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final NegocioRepository negocioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RecaptchaService recaptchaService;

    public AuthController(UsuarioRepository usuarioRepository, ClienteRepository clienteRepository,
                           NegocioRepository negocioRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
                           RecaptchaService recaptchaService) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.negocioRepository = negocioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.recaptchaService = recaptchaService;
    }

    // CU-02: registro de cuenta (Cliente o Admin de un negocio)
    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody RegistroRequest request) {
        // Política de seguridad: el registro también pasa por reCAPTCHA, igual que el
        // login, para que no se puedan crear cuentas en masa con un script.
        if (!recaptchaService.esValido(request.getRecaptchaToken())) {
            return ResponseEntity.badRequest().body("Verificación de seguridad fallida. Vuelve a marcar el reCAPTCHA.");
        }

        // Política de seguridad: contraseña mínima (se valida también en el frontend,
        // pero la que de verdad protege los datos es esta, del lado del servidor).
        String errorPassword = PasswordPolicy.mensajeDeError(request.getPassword());
        if (errorPassword != null) {
            return ResponseEntity.badRequest().body(errorPassword);
        }

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
            negocio.setLatitud(request.getLatitudNegocio());
            negocio.setLongitud(request.getLongitudNegocio());
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
        // NUEVO: verificamos el reCAPTCHA antes de siquiera mirar las credenciales
        if (!recaptchaService.esValido(request.getRecaptchaToken())) {
            return ResponseEntity.badRequest().body("Verificación de seguridad fallida. Vuelve a marcar el reCAPTCHA.");
        }

        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo()).orElse(null);

        if (usuario == null || !passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Correo o contraseña incorrectos.");
        }

        String token = jwtUtil.generarToken(usuario.getCorreo(), usuario.getRol());
        return ResponseEntity.ok(new AuthResponse(token, usuario.getNombre(), usuario.getCorreo(), usuario.getRol()));
    }

    // CU-01: iniciar sesión (o registrarse automáticamente la primera vez) con Google o Facebook
    @PostMapping("/firebase")
    public ResponseEntity<?> loginConFirebase(@RequestBody FirebaseLoginRequest request) {
        FirebaseToken decodedToken;
        try {
            // Este es el paso clave de seguridad: verificamos el token directamente
            // con los servidores de Firebase, no confiamos en nada de lo que venga
            // del navegador sin revisar. Si alguien manda un token falso, esto falla.
            decodedToken = FirebaseAuth.getInstance().verifyIdToken(request.getIdToken());
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No se pudo verificar la sesión de Google/Facebook.");
        } catch (IllegalStateException e) {
            // esto pasa si el backend no tiene configurada la clave de servicio de Firebase todavia
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("El inicio de sesión con Google/Facebook no está configurado en el servidor todavía.");
        }

        String correo = decodedToken.getEmail();
        String nombre = decodedToken.getName() != null ? decodedToken.getName() : correo;

        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);

        if (usuario == null) {
            // primera vez que esta persona entra: la registramos con el rol que
            // pidió el frontend (por defecto, CLIENTE)
            String rol = request.getRol() != null && !request.getRol().isBlank() ? request.getRol() : "CLIENTE";
            usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setCorreo(correo);
            usuario.setPasswordHash(passwordEncoder.encode(java.util.UUID.randomUUID().toString()));
            usuario.setRol(rol);
            usuarioRepository.save(usuario);

            if ("CLIENTE".equals(rol)) {
                Cliente cliente = new Cliente();
                cliente.setNombre(nombre);
                cliente.setCorreo(correo);
                clienteRepository.save(cliente);
            }
            // Nota: si el rol es ADMIN_NEGOCIO, el registro del negocio (nombre,
            // rubro, ubicacion, etc.) se completa despues desde "Mi negocio" en
            // Ajustes, ya que Google/Facebook no entregan esos datos.
        }

        String token = jwtUtil.generarToken(usuario.getCorreo(), usuario.getRol());
        return ResponseEntity.ok(new AuthResponse(token, usuario.getNombre(), usuario.getCorreo(), usuario.getRol()));
    }
}
