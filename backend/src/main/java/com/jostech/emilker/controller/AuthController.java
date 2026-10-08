package com.jostech.emilker.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.jostech.emilker.dto.AuthResponse;
import com.jostech.emilker.dto.FirebaseLoginRequest;
import com.jostech.emilker.dto.LoginRequest;
import com.jostech.emilker.dto.RecuperarPasswordRequest;
import com.jostech.emilker.dto.RegistroRequest;
import com.jostech.emilker.dto.RestablecerPasswordRequest;
import com.jostech.emilker.model.Cliente;
import com.jostech.emilker.model.Negocio;
import com.jostech.emilker.model.Usuario;
import com.jostech.emilker.repository.ClienteRepository;
import com.jostech.emilker.repository.NegocioRepository;
import com.jostech.emilker.repository.UsuarioRepository;
import com.jostech.emilker.security.JwtUtil;
import com.jostech.emilker.service.RecaptchaService;
import com.jostech.emilker.util.PasswordPolicy;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

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
    // ObjectProvider: si no hay spring.mail.host configurado (como en RecordatorioService),
    // no existe un bean JavaMailSender y la app sigue arrancando igual; el link de
    // restablecimiento simplemente queda registrado en el log en vez de enviarse por correo real.
    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    // URL del frontend ya publicado, para armar el link de "restablecer contraseña"
    // que se manda por correo. Variable de entorno FRONTEND_URL en el servidor real.
    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    private static final long RESET_TOKEN_VIGENCIA_MINUTOS = 30;

    public AuthController(UsuarioRepository usuarioRepository, ClienteRepository clienteRepository,
                           NegocioRepository negocioRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
                           RecaptchaService recaptchaService, ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.negocioRepository = negocioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.recaptchaService = recaptchaService;
        this.mailSenderProvider = mailSenderProvider;
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

    // CU: recuperar contraseña olvidada — paso 1, el usuario pide el link
    @PostMapping("/recuperar")
    public ResponseEntity<?> recuperarPassword(@RequestBody RecuperarPasswordRequest request) {
        // Mensaje de respuesta SIEMPRE igual, exista o no esa cuenta. Es una política
        // de seguridad a propósito: si dijéramos "ese correo no existe" cualquiera
        // podría usar este formulario para averiguar qué correos están registrados.
        String mensaje = "Si ese correo está registrado, te enviamos un enlace para restablecer tu contraseña.";

        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo()).orElse(null);
        if (usuario != null) {
            String token = UUID.randomUUID().toString();
            usuario.setResetToken(token);
            usuario.setResetTokenExpira(Instant.now().plus(RESET_TOKEN_VIGENCIA_MINUTOS, ChronoUnit.MINUTES));
            usuarioRepository.save(usuario);

            String link = frontendUrl + "/restablecer-password?token=" + token;
            JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
            if (mailSender != null) {
                try {
                    SimpleMailMessage correo = new SimpleMailMessage();
                    correo.setTo(usuario.getCorreo());
                    correo.setSubject("Restablece tu contraseña de Emilker Barber Shop");
                    correo.setText("Hola " + usuario.getNombre() + ",\n\n"
                            + "Recibimos una solicitud para restablecer tu contraseña. Este enlace es válido "
                            + "por " + RESET_TOKEN_VIGENCIA_MINUTOS + " minutos:\n\n" + link + "\n\n"
                            + "Si tú no pediste esto, puedes ignorar este correo; tu contraseña sigue igual.");
                    mailSender.send(correo);
                } catch (Exception errorEnvio) {
                    log.warn("No se pudo enviar el correo de restablecimiento a {}: {}", usuario.getCorreo(), errorEnvio.getMessage());
                }
            } else {
                // Sin correo configurado en application.properties (modo local/desarrollo):
                // dejamos el link en el log para poder probar el flujo igual.
                log.info("[Restablecer contraseña] Correo no configurado. Link para {}: {}", usuario.getCorreo(), link);
            }
        }

        return ResponseEntity.ok(mensaje);
    }

    // CU: recuperar contraseña olvidada — paso 2, el usuario entra con el token del link y pone una nueva
    @PostMapping("/restablecer")
    public ResponseEntity<?> restablecerPassword(@RequestBody RestablecerPasswordRequest request) {
        if (request.getToken() == null || request.getToken().isBlank()) {
            return ResponseEntity.badRequest().body("El enlace de restablecimiento no es válido.");
        }

        Usuario usuario = usuarioRepository.findByResetToken(request.getToken()).orElse(null);
        if (usuario == null || usuario.getResetTokenExpira() == null
                || usuario.getResetTokenExpira().isBefore(Instant.now())) {
            return ResponseEntity.badRequest().body("El enlace ya expiró o no es válido. Pide uno nuevo.");
        }

        String errorPassword = PasswordPolicy.mensajeDeError(request.getNuevaPassword());
        if (errorPassword != null) {
            return ResponseEntity.badRequest().body(errorPassword);
        }

        usuario.setPasswordHash(passwordEncoder.encode(request.getNuevaPassword()));
        // El token es de un solo uso: lo limpiamos apenas se usa, para que ese mismo
        // link no sirva una segunda vez.
        usuario.setResetToken(null);
        usuario.setResetTokenExpira(null);
        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Tu contraseña se actualizó correctamente. Ya puedes iniciar sesión.");
    }
}
