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

        if (negocioRepository.count() == 0) {
            // ---- 1. Emilker Barber Shop (barbería, Yopal) ----
            Negocio emilker = negocio("Emilker Barber Shop", "BARBERIA", "(608) 123 4567", "Casanare",
                    "Calle principal, Yopal", "emilker@barbershop.com", true,
                    "https://picsum.photos/seed/emilker-logo/200/200", "https://picsum.photos/seed/emilker-barber/900/400",
                    5.3378, -72.3959, "Corte básico", 15000, "573001112233");
            usuarioRepository.save(usuario("Emilker", "emilker@barbershop.com", "emilker123", "ADMIN_NEGOCIO"));
            List<Barbero> emilkerEquipo = List.of(
                    barbero(emilker.getId(), "Emilker", "https://i.pravatar.cc/300?img=12", "573001112244"),
                    barbero(emilker.getId(), "Andrés Felipe", "https://i.pravatar.cc/300?img=33", "573001112255"),
                    barbero(emilker.getId(), "Carlos Torres", "https://i.pravatar.cc/300?img=51", "573001112266")
            );
            barberoRepository.saveAll(emilkerEquipo);
            productoRepository.saveAll(List.of(
                    producto(emilker.getId(), "Shampoo Premium", 35000, 8, 5, "https://picsum.photos/seed/shampoo-kutt/400/400"),
                    producto(emilker.getId(), "Cera Mate", 28000, 5, 5, "https://picsum.photos/seed/cera-kutt/400/400"),
                    producto(emilker.getId(), "Aceite para barba", 25000, 3, 5, "https://picsum.photos/seed/aceite-kutt/400/400")
            ));
            adicionalRepository.saveAll(List.of(
                    adicional(emilker.getId(), "Barba", 8000),
                    adicional(emilker.getId(), "Cejas", 5000),
                    adicional(emilker.getId(), "Tinte", 15000)
            ));
            metodoPagoRepository.saveAll(List.of(
                    metodoPago(emilker.getId(), "Bancolombia", "DIGITAL", "Ahorros ****4521"),
                    metodoPago(emilker.getId(), "Nequi", "DIGITAL", "300 111 2233"),
                    metodoPago(emilker.getId(), "Efectivo", "EFECTIVO", "Pago en el local")
            ));
            citaRepository.saveAll(List.of(
                    cita(emilker.getId(), "Juan David", "312 456 7890", "Corte básico", "Emilker", "Barba", "10 mayo", "10:00 AM", "Confirmada", 23000),
                    cita(emilker.getId(), "Carlos Torres", "320 123 4567", "Corte básico", "Emilker", "Barba, Tinte", "10 mayo", "11:30 AM", "Confirmada", 38000),
                    cita(emilker.getId(), "Andrés Felipe", "311 987 6543", "Corte básico", "Andrés Felipe", "", "10 mayo", "2:00 PM", "Pendiente", 15000)
            ));
            resenaRepository.saveAll(List.of(
                    resena(emilker.getId(), "Juan David", "juan@correo.com", 5, "Excelente atención, muy puntuales.", "5 mayo"),
                    resena(emilker.getId(), "Carlos Torres", "carlos@correo.com", 4, "Buen servicio, el local es muy cómodo.", "3 mayo")
            ));

            // ---- 2. Nails Studio (uñas, Yopal) ----
            Negocio nails = negocio("Nails Studio", "UNAS", "(608) 234 5678", "Casanare",
                    "Cra 20 # 15-30, Yopal", "contacto@nailsstudio.com", true,
                    "https://picsum.photos/seed/nails-logo/200/200", "https://picsum.photos/seed/nails-studio/900/400",
                    5.3401, -72.3925, "Manicure clásica", 18000, "573002223344");
            usuarioRepository.save(usuario("Nails Studio", "contacto@nailsstudio.com", "nails123", "ADMIN_NEGOCIO"));
            barberoRepository.saveAll(List.of(
                    barbero(nails.getId(), "Laura Gómez", "https://i.pravatar.cc/300?img=45", "573002223355"),
                    barbero(nails.getId(), "Valentina Ríos", "https://i.pravatar.cc/300?img=47", "573002223366")
            ));
            productoRepository.saveAll(List.of(
                    producto(nails.getId(), "Esmalte semipermanente", 22000, 12, 4, "https://picsum.photos/seed/esmalte-kutt/400/400"),
                    producto(nails.getId(), "Kit de manicure", 40000, 6, 3, "https://picsum.photos/seed/kitmanicure-kutt/400/400"),
                    producto(nails.getId(), "Aceite para cutícula", 15000, 10, 4, "https://picsum.photos/seed/cuticula-kutt/400/400")
            ));
            adicionalRepository.saveAll(List.of(
                    adicional(nails.getId(), "Decoración de uñas", 10000),
                    adicional(nails.getId(), "Retiro de esmalte", 5000)
            ));
            metodoPagoRepository.saveAll(List.of(
                    metodoPago(nails.getId(), "Nequi", "DIGITAL", "300 222 3344"),
                    metodoPago(nails.getId(), "Efectivo", "EFECTIVO", "Pago en el local")
            ));
            resenaRepository.save(resena(nails.getId(), "Andrés Felipe", "andres@correo.com", 5, "Quedaron divinas, muy detallistas.", "2 mayo"));

            // ---- 3. Glow Estética (estética, Yopal) ----
            Negocio glow = negocio("Glow Estética", "ESTETICA", "(608) 345 6789", "Casanare",
                    "Av. Central # 8-12, Yopal", "contacto@glowestetica.com", false,
                    "https://picsum.photos/seed/glow-logo/200/200", "https://picsum.photos/seed/glow-estetica/900/400",
                    5.3350, -72.4001, "Limpieza facial profunda", 45000, "573003334455");
            usuarioRepository.save(usuario("Glow Estética", "contacto@glowestetica.com", "glow123", "ADMIN_NEGOCIO"));
            barberoRepository.saveAll(List.of(
                    barbero(glow.getId(), "Mariana López", "https://i.pravatar.cc/300?img=25", "573003334466"),
                    barbero(glow.getId(), "Camila Rojas", "https://i.pravatar.cc/300?img=28", "573003334477")
            ));
            productoRepository.saveAll(List.of(
                    producto(glow.getId(), "Sérum facial", 55000, 7, 3, "https://picsum.photos/seed/serum-kutt/400/400"),
                    producto(glow.getId(), "Mascarilla de arcilla", 32000, 9, 4, "https://picsum.photos/seed/arcilla-kutt/400/400")
            ));
            adicionalRepository.saveAll(List.of(
                    adicional(glow.getId(), "Exfoliación corporal", 20000),
                    adicional(glow.getId(), "Masaje relajante", 30000)
            ));
            metodoPagoRepository.saveAll(List.of(
                    metodoPago(glow.getId(), "Bancolombia", "DIGITAL", "Ahorros ****7788"),
                    metodoPago(glow.getId(), "Efectivo", "EFECTIVO", "Pago en el local")
            ));

            // ---- 4. NUEVO: El Rey del Fade Barbería (barbería, Yopal, otro sector) ----
            Negocio rey = negocio("El Rey del Fade Barbería", "BARBERIA", "(608) 456 7890", "Casanare",
                    "Cra 24 # 10-45, Yopal", "elrey@barbershop.com", true,
                    "https://picsum.photos/seed/reyfade-logo/200/200", "https://picsum.photos/seed/reyfade-portada/900/400",
                    5.3512, -72.4102, "Corte + diseño de fade", 20000, "573004445566");
            usuarioRepository.save(usuario("El Rey del Fade", "elrey@barbershop.com", "reyfade123", "ADMIN_NEGOCIO"));
            barberoRepository.saveAll(List.of(
                    barbero(rey.getId(), "Jhon Fredy Castro", "https://i.pravatar.cc/300?img=14", "573004445577"),
                    barbero(rey.getId(), "Steven Ramírez", "https://i.pravatar.cc/300?img=15", "573004445588")
            ));
            productoRepository.saveAll(List.of(
                    producto(rey.getId(), "Pomada mate", 26000, 10, 4, "https://picsum.photos/seed/pomada-kutt/400/400"),
                    producto(rey.getId(), "Loción para después de afeitar", 18000, 6, 3, "https://picsum.photos/seed/locion-kutt/400/400")
            ));
            adicionalRepository.saveAll(List.of(
                    adicional(rey.getId(), "Diseño de línea", 6000),
                    adicional(rey.getId(), "Barba", 8000)
            ));
            metodoPagoRepository.saveAll(List.of(
                    metodoPago(rey.getId(), "Nequi", "DIGITAL", "300 444 5566"),
                    metodoPago(rey.getId(), "Efectivo", "EFECTIVO", "Pago en el local")
            ));
            resenaRepository.save(resena(rey.getId(), "Juan David", "juan@correo.com", 5, "El mejor fade de Yopal, sin duda.", "1 mayo"));

            // ---- 5. NUEVO: Uñas & Detalles (uñas, Yopal, otro sector) ----
            Negocio unasDetalles = negocio("Uñas & Detalles", "UNAS", "(608) 567 8901", "Casanare",
                    "Cll 12 # 18-20, Yopal", "unasydetalles@correo.com", false,
                    "https://picsum.photos/seed/unasdetalles-logo/200/200", "https://picsum.photos/seed/unasdetalles-portada/900/400",
                    5.3298, -72.3872, "Manicure spa", 25000, "573005556677");
            usuarioRepository.save(usuario("Uñas & Detalles", "unasydetalles@correo.com", "unas123", "ADMIN_NEGOCIO"));
            barberoRepository.saveAll(List.of(
                    barbero(unasDetalles.getId(), "Daniela Peña", "https://i.pravatar.cc/300?img=48", "573005556688"),
                    barbero(unasDetalles.getId(), "Yesenia Morales", "https://i.pravatar.cc/300?img=49", "573005556699")
            ));
            productoRepository.saveAll(List.of(
                    producto(unasDetalles.getId(), "Lámpara UV portátil", 60000, 4, 2, "https://picsum.photos/seed/lampara-kutt/400/400"),
                    producto(unasDetalles.getId(), "Set de esmaltes", 30000, 8, 3, "https://picsum.photos/seed/setesmaltes-kutt/400/400")
            ));
            adicionalRepository.saveAll(List.of(
                    adicional(unasDetalles.getId(), "Uñas acrílicas", 25000),
                    adicional(unasDetalles.getId(), "Pedicure spa", 20000)
            ));
            metodoPagoRepository.saveAll(List.of(
                    metodoPago(unasDetalles.getId(), "Nequi", "DIGITAL", "300 555 6677"),
                    metodoPago(unasDetalles.getId(), "Efectivo", "EFECTIVO", "Pago en el local")
            ));

            // ---- 6. NUEVO: Oasis Spa & Relax (spa, Yopal) ----
            Negocio oasis = negocio("Oasis Spa & Relax", "SPA", "(608) 678 9012", "Casanare",
                    "Cll 8 # 22-40, Yopal", "oasis@spa.com", true,
                    "https://picsum.photos/seed/oasis-logo/200/200", "https://picsum.photos/seed/oasis-portada/900/400",
                    5.3445, -72.4055, "Masaje relajante 60 min", 60000, "573006667788");
            usuarioRepository.save(usuario("Oasis Spa", "oasis@spa.com", "oasis123", "ADMIN_NEGOCIO"));
            barberoRepository.saveAll(List.of(
                    barbero(oasis.getId(), "Paula Sánchez", "https://i.pravatar.cc/300?img=32", "573006667799"),
                    barbero(oasis.getId(), "Ricardo Vega", "https://i.pravatar.cc/300?img=13", "573006668800")
            ));
            productoRepository.saveAll(List.of(
                    producto(oasis.getId(), "Aceite esencial de lavanda", 28000, 9, 3, "https://picsum.photos/seed/lavanda-kutt/400/400"),
                    producto(oasis.getId(), "Sales de baño", 22000, 7, 3, "https://picsum.photos/seed/salesbano-kutt/400/400")
            ));
            adicionalRepository.saveAll(List.of(
                    adicional(oasis.getId(), "Piedras calientes", 25000),
                    adicional(oasis.getId(), "Aromaterapia", 15000)
            ));
            metodoPagoRepository.saveAll(List.of(
                    metodoPago(oasis.getId(), "Bancolombia", "DIGITAL", "Ahorros ****3311"),
                    metodoPago(oasis.getId(), "Efectivo", "EFECTIVO", "Pago en el local")
            ));
            resenaRepository.save(resena(oasis.getId(), "Carlos Torres", "carlos@correo.com", 5, "Salí completamente relajado, muy recomendado.", "28 abril"));

            // ---- 7. NUEVO: Zen Spa Casanare (spa, Aguazul — otro municipio) ----
            Negocio zen = negocio("Zen Spa Casanare", "SPA", "(608) 789 0123", "Casanare",
                    "Cll 10 # 9-15, Aguazul", "zen@spa.com", false,
                    "https://picsum.photos/seed/zen-logo/200/200", "https://picsum.photos/seed/zen-portada/900/400",
                    5.1667, -72.5500, "Terapia de relajación", 55000, "573007778899");
            usuarioRepository.save(usuario("Zen Spa", "zen@spa.com", "zen123", "ADMIN_NEGOCIO"));
            barberoRepository.saveAll(List.of(
                    barbero(zen.getId(), "Sofía Herrera", "https://i.pravatar.cc/300?img=29", "573007778900"),
                    barbero(zen.getId(), "Manuel Beltrán", "https://i.pravatar.cc/300?img=18", "573007778911")
            ));
            productoRepository.saveAll(List.of(
                    producto(zen.getId(), "Vela aromática", 18000, 10, 4, "https://picsum.photos/seed/velaaroma-kutt/400/400"),
                    producto(zen.getId(), "Loción corporal relajante", 26000, 6, 3, "https://picsum.photos/seed/locioncorporal-kutt/400/400")
            ));
            adicionalRepository.saveAll(List.of(
                    adicional(zen.getId(), "Reflexología", 20000),
                    adicional(zen.getId(), "Masaje de espalda", 25000)
            ));
            metodoPagoRepository.saveAll(List.of(
                    metodoPago(zen.getId(), "Nequi", "DIGITAL", "300 777 8899"),
                    metodoPago(zen.getId(), "Efectivo", "EFECTIVO", "Pago en el local")
            ));

            // ---- 8. NUEVO: Bambú Spa (spa, Yopal, otro sector) ----
            Negocio bambu = negocio("Bambú Spa", "SPA", "(608) 890 1234", "Casanare",
                    "Cra 30 # 5-60, Yopal", "bambu@spa.com", false,
                    "https://picsum.photos/seed/bambu-logo/200/200", "https://picsum.photos/seed/bambu-portada/900/400",
                    5.3255, -72.4150, "Masaje descontracturante", 50000, "573008889900");
            usuarioRepository.save(usuario("Bambú Spa", "bambu@spa.com", "bambu123", "ADMIN_NEGOCIO"));
            barberoRepository.saveAll(List.of(
                    barbero(bambu.getId(), "Isabela Cárdenas", "https://i.pravatar.cc/300?img=41", "573008889911"),
                    barbero(bambu.getId(), "Felipe Guzmán", "https://i.pravatar.cc/300?img=16", "573008889922")
            ));
            productoRepository.saveAll(List.of(
                    producto(bambu.getId(), "Crema hidratante corporal", 24000, 8, 3, "https://picsum.photos/seed/cremacorporal-kutt/400/400"),
                    producto(bambu.getId(), "Difusor de aromas", 45000, 5, 2, "https://picsum.photos/seed/difusor-kutt/400/400")
            ));
            adicionalRepository.saveAll(List.of(
                    adicional(bambu.getId(), "Masaje de pies", 18000),
                    adicional(bambu.getId(), "Exfoliación con sal marina", 22000)
            ));
            metodoPagoRepository.saveAll(List.of(
                    metodoPago(bambu.getId(), "Bancolombia", "DIGITAL", "Ahorros ****9922"),
                    metodoPago(bambu.getId(), "Efectivo", "EFECTIVO", "Pago en el local")
            ));

            // ---- 9. NUEVO: Piel Perfecta Estética (estética, Yopal, otro sector) ----
            Negocio pielPerfecta = negocio("Piel Perfecta Estética", "ESTETICA", "(608) 901 2345", "Casanare",
                    "Cll 15 # 21-10, Yopal", "pielperfecta@correo.com", true,
                    "https://picsum.photos/seed/pielperfecta-logo/200/200", "https://picsum.photos/seed/pielperfecta-portada/900/400",
                    5.3420, -72.3800, "Limpieza facial + hidratación", 48000, "573009990011");
            usuarioRepository.save(usuario("Piel Perfecta", "pielperfecta@correo.com", "piel123", "ADMIN_NEGOCIO"));
            barberoRepository.saveAll(List.of(
                    barbero(pielPerfecta.getId(), "Natalia Suárez", "https://i.pravatar.cc/300?img=24", "573009990022"),
                    barbero(pielPerfecta.getId(), "Diana Cortés", "https://i.pravatar.cc/300?img=26", "573009990033")
            ));
            productoRepository.saveAll(List.of(
                    producto(pielPerfecta.getId(), "Protector solar facial", 38000, 9, 3, "https://picsum.photos/seed/protectorsolar-kutt/400/400"),
                    producto(pielPerfecta.getId(), "Contorno de ojos", 42000, 6, 3, "https://picsum.photos/seed/contornoojos-kutt/400/400")
            ));
            adicionalRepository.saveAll(List.of(
                    adicional(pielPerfecta.getId(), "Peeling facial", 35000),
                    adicional(pielPerfecta.getId(), "Hidratación profunda", 25000)
            ));
            metodoPagoRepository.saveAll(List.of(
                    metodoPago(pielPerfecta.getId(), "Nequi", "DIGITAL", "300 999 0011"),
                    metodoPago(pielPerfecta.getId(), "Efectivo", "EFECTIVO", "Pago en el local")
            ));
            resenaRepository.save(resena(pielPerfecta.getId(), "Andrés Felipe", "andres@correo.com", 5, "Mi piel quedó increíble, muy profesionales.", "27 abril"));

            // ---- 10. NUEVO: Renova Estética Integral (estética, Villanueva — otro municipio) ----
            Negocio renova = negocio("Renova Estética Integral", "ESTETICA", "(608) 012 3456", "Casanare",
                    "Cll 6 # 7-20, Villanueva", "renova@correo.com", false,
                    "https://picsum.photos/seed/renova-logo/200/200", "https://picsum.photos/seed/renova-portada/900/400",
                    4.6103, -72.9578, "Tratamiento facial integral", 52000, "573000112233");
            usuarioRepository.save(usuario("Renova Estética", "renova@correo.com", "renova123", "ADMIN_NEGOCIO"));
            barberoRepository.saveAll(List.of(
                    barbero(renova.getId(), "Lorena Jiménez", "https://i.pravatar.cc/300?img=36", "573000112244"),
                    barbero(renova.getId(), "Gabriel Pardo", "https://i.pravatar.cc/300?img=17", "573000112255")
            ));
            productoRepository.saveAll(List.of(
                    producto(renova.getId(), "Ampolletas de colágeno", 50000, 6, 2, "https://picsum.photos/seed/colageno-kutt/400/400"),
                    producto(renova.getId(), "Crema antiedad", 60000, 5, 2, "https://picsum.photos/seed/antiedad-kutt/400/400")
            ));
            adicionalRepository.saveAll(List.of(
                    adicional(renova.getId(), "Radiofrecuencia facial", 45000),
                    adicional(renova.getId(), "Masaje reductor", 30000)
            ));
            metodoPagoRepository.saveAll(List.of(
                    metodoPago(renova.getId(), "Bancolombia", "DIGITAL", "Ahorros ****5544"),
                    metodoPago(renova.getId(), "Efectivo", "EFECTIVO", "Pago en el local")
            ));
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
