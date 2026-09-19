package com.jostech.emilker.config;

import com.jostech.emilker.model.Adicional;
import com.jostech.emilker.model.Barbero;
import com.jostech.emilker.model.Cita;
import com.jostech.emilker.model.Cliente;
import com.jostech.emilker.model.Configuracion;
import com.jostech.emilker.model.MetodoPago;
import com.jostech.emilker.model.Negocio;
import com.jostech.emilker.model.Producto;
import com.jostech.emilker.model.Usuario;
import com.jostech.emilker.repository.AdicionalRepository;
import com.jostech.emilker.repository.BarberoRepository;
import com.jostech.emilker.repository.CitaRepository;
import com.jostech.emilker.repository.ClienteRepository;
import com.jostech.emilker.repository.ConfiguracionRepository;
import com.jostech.emilker.repository.MetodoPagoRepository;
import com.jostech.emilker.repository.NegocioRepository;
import com.jostech.emilker.repository.ProductoRepository;
import com.jostech.emilker.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AdicionalRepository adicionalRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final ConfiguracionRepository configuracionRepository;
    private final BarberoRepository barberoRepository;
    private final NegocioRepository negocioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(ClienteRepository clienteRepository, ProductoRepository productoRepository,
                       CitaRepository citaRepository, UsuarioRepository usuarioRepository,
                       AdicionalRepository adicionalRepository, MetodoPagoRepository metodoPagoRepository,
                       ConfiguracionRepository configuracionRepository, BarberoRepository barberoRepository,
                       NegocioRepository negocioRepository, PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.citaRepository = citaRepository;
        this.usuarioRepository = usuarioRepository;
        this.adicionalRepository = adicionalRepository;
        this.metodoPagoRepository = metodoPagoRepository;
        this.configuracionRepository = configuracionRepository;
        this.barberoRepository = barberoRepository;
        this.negocioRepository = negocioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (clienteRepository.count() == 0) {
            clienteRepository.save(cliente("Juan David", "juan@correo.com", "312 456 7890"));
            clienteRepository.save(cliente("Carlos Torres", "carlos@correo.com", "320 123 4567"));
            clienteRepository.save(cliente("Andrés Felipe", "andres@correo.com", "311 987 6543"));
        }

        if (productoRepository.count() == 0) {
            productoRepository.save(producto("Shampoo Premium", 35000, 8, 5));
            productoRepository.save(producto("Cera Mate", 28000, 5, 5));
            productoRepository.save(producto("Aceite para barba", 25000, 3, 5));
            productoRepository.save(producto("Gel Fijador", 20000, 6, 5));
            productoRepository.save(producto("Spray Texturizante", 30000, 4, 5));
        }

        if (adicionalRepository.count() == 0) {
            adicionalRepository.save(adicional("Barba", 8000));
            adicionalRepository.save(adicional("Cejas", 5000));
            adicionalRepository.save(adicional("Tinte", 15000));
            adicionalRepository.save(adicional("Bigote", 3000));
            adicionalRepository.save(adicional("Retoque de pestañas", 10000));
        }

        if (metodoPagoRepository.count() == 0) {
            metodoPagoRepository.save(metodoPago("Bancolombia", "DIGITAL", "Ahorros ****4521"));
            metodoPagoRepository.save(metodoPago("Nequi", "DIGITAL", "300 123 4567"));
            metodoPagoRepository.save(metodoPago("Efectivo", "EFECTIVO", "Pago en el local"));
        }

        if (configuracionRepository.count() == 0) {
            Configuracion config = new Configuracion();
            config.setId(1L);
            config.setServicioNombre("Corte básico");
            config.setServicioPrecio(15000);
            config.setNotificacionDestino("emilker@barbershop.com");
            configuracionRepository.save(config);
        }

        if (barberoRepository.count() == 0) {
            barberoRepository.save(barbero("Emilker", "https://i.pravatar.cc/300?img=12"));
            barberoRepository.save(barbero("Andrés Felipe", "https://i.pravatar.cc/300?img=33"));
            barberoRepository.save(barbero("Carlos Torres", "https://i.pravatar.cc/300?img=51"));
        }

        if (citaRepository.count() == 0) {
            citaRepository.save(cita("Juan David", "312 456 7890", "Emilker", "Barba", "10 mayo", "10:00 AM", "Confirmada", 23000));
            citaRepository.save(cita("Carlos Torres", "320 123 4567", "Emilker", "Barba, Tinte", "10 mayo", "11:30 AM", "Confirmada", 38000));
            citaRepository.save(cita("Andrés Felipe", "311 987 6543", "Andrés Felipe", "", "10 mayo", "2:00 PM", "Pendiente", 15000));
        }

        if (usuarioRepository.count() == 0) {
            usuarioRepository.save(usuario("Emilker", "emilker@barbershop.com", "emilker123", "ADMIN_NEGOCIO"));
            usuarioRepository.save(usuario("Juan David", "juan@correo.com", "cliente123", "CLIENTE"));
        }

        if (negocioRepository.count() == 0) {
            Negocio negocio = new Negocio();
            negocio.setNombre("Emilker Barber Shop");
            negocio.setRubro("BARBERIA");
            negocio.setTelefonoFijo("(608) 123 4567");
            negocio.setDepartamento("Casanare");
            negocio.setDireccion("Calle principal, Yopal");
            negocio.setAdminCorreo("emilker@barbershop.com");
            negocio.setVerificado(true);
            negocioRepository.save(negocio);
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

    private Producto producto(String nombre, double precio, int existencias, int minimo) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setPrecio(precio);
        p.setExistencias(existencias);
        p.setMinimo(minimo);
        return p;
    }

    private Adicional adicional(String nombre, double precio) {
        Adicional a = new Adicional();
        a.setNombre(nombre);
        a.setPrecio(precio);
        return a;
    }

    private MetodoPago metodoPago(String nombre, String tipo, String cuenta) {
        MetodoPago m = new MetodoPago();
        m.setNombre(nombre);
        m.setTipo(tipo);
        m.setCuenta(cuenta);
        return m;
    }

    private Barbero barbero(String nombre, String fotoUrl) {
        Barbero b = new Barbero();
        b.setNombre(nombre);
        b.setFotoUrl(fotoUrl);
        b.setActivo(true);
        return b;
    }

    private Cita cita(String cliente, String telefono, String barbero, String adicionales, String fecha, String hora, String estado, double total) {
        Cita c = new Cita();
        c.setCliente(cliente);
        c.setTelefonoCliente(telefono);
        c.setServicio("Corte básico");
        c.setBarbero(barbero);
        c.setAdicionales(adicionales);
        c.setFecha(fecha);
        c.setHora(hora);
        c.setEstado(estado);
        c.setTotal(total);
        c.setEstadoPago("Pendiente");
        return c;
    }
}
