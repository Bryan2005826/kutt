package com.jostech.emilker.config;

import com.jostech.emilker.model.Adicional;
import com.jostech.emilker.model.Barbero;
import com.jostech.emilker.model.Cita;
import com.jostech.emilker.model.Cliente;
import com.jostech.emilker.model.MetodoPago;
import com.jostech.emilker.model.Negocio;
import com.jostech.emilker.model.Producto;
import com.jostech.emilker.model.Resena;
import com.jostech.emilker.model.Usuario;
import com.jostech.emilker.repository.AdicionalRepository;
import com.jostech.emilker.repository.BarberoRepository;
import com.jostech.emilker.repository.CitaRepository;
import com.jostech.emilker.repository.ClienteRepository;
import com.jostech.emilker.repository.MetodoPagoRepository;
import com.jostech.emilker.repository.NegocioRepository;
import com.jostech.emilker.repository.ProductoRepository;
import com.jostech.emilker.repository.ResenaRepository;
import com.jostech.emilker.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

// Multi-tenant real: cada negocio del directorio tiene su propia cuenta admin,
// su propio equipo (barberos/estilistas/terapeutas), sus propios productos,
// adicionales y metodos de pago. Nada se comparte entre negocios.
@Component
public class DataSeeder implements CommandLineRunner {

    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AdicionalRepository adicionalRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final BarberoRepository barberoRepository;
    private final NegocioRepository negocioRepository;
    private final ResenaRepository resenaRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(ClienteRepository clienteRepository, ProductoRepository productoRepository,
                       CitaRepository citaRepository, UsuarioRepository usuarioRepository,
                       AdicionalRepository adicionalRepository, MetodoPagoRepository metodoPagoRepository,
                       BarberoRepository barberoRepository, NegocioRepository negocioRepository,
                       ResenaRepository resenaRepository, PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.citaRepository = citaRepository;
        this.usuarioRepository = usuarioRepository;
        this.adicionalRepository = adicionalRepository;
        this.metodoPagoRepository = metodoPagoRepository;
        this.barberoRepository = barberoRepository;
        this.negocioRepository = negocioRepository;
        this.resenaRepository = resenaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (clienteRepository.count() == 0) {
            clienteRepository.save(cliente("Juan David", "juan@correo.com", "312 456 7890"));
            clienteRepository.save(cliente("Carlos Torres", "carlos@correo.com", "320 123 4567"));
            clienteRepository.save(cliente("Andrés Felipe", "andres@correo.com", "311 987 6543"));
        }

        if (usuarioRepository.count() == 0) {
            usuarioRepository.save(usuario("Juan David", "juan@correo.com", "cliente123", "CLIENTE"));
            usuarioRepository.save(usuario("Kutt", "admin@kutt.com", "kutt123", "SUPER_ADMIN"));
        }
    }

    private Usuario usuario(String nombre, String correo, String passwordPlano, String rol) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setCorreo(correo);
        u.setPasswordHash(passwordEncoder.encode(passwordPlano));
        u.setRol(rol);
        return u;
    }

    private Cliente cliente(String nombre, String correo, String telefono) {
        Cliente c = new Cliente();
        c.setNombre(nombre);
        c.setCorreo(correo);
        c.setTelefono(telefono);
        return c;
    }

    private Producto producto(Long negocioId, String nombre, double precio, int existencias, int minimo, String fotoUrl) {
        Producto p = new Producto();
        p.setNegocioId(negocioId);
        p.setNombre(nombre);
        p.setPrecio(precio);
        p.setExistencias(existencias);
        p.setMinimo(minimo);
        p.setFotoUrl(fotoUrl);
        return p;
    }

    private Adicional adicional(Long negocioId, String nombre, double precio) {
        Adicional a = new Adicional();
        a.setNegocioId(negocioId);
        a.setNombre(nombre);
        a.setPrecio(precio);
        return a;
    }

    private MetodoPago metodoPago(Long negocioId, String nombre, String tipo, String cuenta) {
        MetodoPago m = new MetodoPago();
        m.setNegocioId(negocioId);
        m.setNombre(nombre);
        m.setTipo(tipo);
        m.setCuenta(cuenta);
        return m;
    }

    private Barbero barbero(Long negocioId, String nombre, String fotoUrl, String whatsapp) {
        Barbero b = new Barbero();
        b.setNegocioId(negocioId);
        b.setNombre(nombre);
        b.setFotoUrl(fotoUrl);
        b.setWhatsapp(whatsapp);
        b.setActivo(true);
        return b;
    }

    private Cita cita(Long negocioId, String cliente, String telefono, String servicio, String barbero,
                       String adicionales, String fecha, String hora, String estado, double total) {
        Cita c = new Cita();
        c.setNegocioId(negocioId);
        c.setCliente(cliente);
        c.setTelefonoCliente(telefono);
        c.setServicio(servicio);
        c.setBarbero(barbero);
        c.setAdicionales(adicionales);
        c.setFecha(fecha);
        c.setHora(hora);
        c.setEstado(estado);
        c.setTotal(total);
        c.setEstadoPago("Pendiente");
        return c;
    }

    private Negocio negocio(String nombre, String rubro, String telefonoFijo, String departamento,
                             String direccion, String adminCorreo, boolean verificado,
                             String logoUrl, String portadaUrl, double lat, double lng,
                             String servicioNombre, double servicioPrecio, String whatsapp) {
        Negocio n = new Negocio();
        n.setNombre(nombre);
        n.setRubro(rubro);
        n.setTelefonoFijo(telefonoFijo);
        n.setDepartamento(departamento);
        n.setDireccion(direccion);
        n.setAdminCorreo(adminCorreo);
        n.setVerificado(verificado);
        n.setLogoUrl(logoUrl);
        n.setPortadaUrl(portadaUrl);
        n.setLatitud(lat);
        n.setLongitud(lng);
        n.setServicioNombre(servicioNombre);
        n.setServicioPrecio(servicioPrecio);
        n.setWhatsapp(whatsapp);
        return negocioRepository.save(n);
    }

    private Resena resena(Long negocioId, String clienteNombre, String clienteCorreo, int calificacion,
                           String comentario, String fecha) {
        Resena r = new Resena();
        r.setNegocioId(negocioId);
        r.setClienteNombre(clienteNombre);
        r.setClienteCorreo(clienteCorreo);
        r.setCalificacion(calificacion);
        r.setComentario(comentario);
        r.setFecha(fecha);
        return r;
    }
}
