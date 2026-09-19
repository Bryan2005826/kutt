# Kutt — Backend

API REST en Java + Spring Boot + MySQL para Kutt, sistema de gestión para barberías.
Caso de implementación: Emilker Barber Shop.

> El paquete Java sigue llamándose `com.jostech.emilker` por continuidad del código — es solo
> un identificador interno, no afecta el nombre del producto ni lo que ve el usuario.

## Requisitos

- Java 17 o superior
- Maven
- MySQL corriendo localmente (por ejemplo con XAMPP), puerto 3306, usuario `root` sin contraseña.
  Si tu MySQL tiene otra contraseña, cámbiala en `src/main/resources/application.properties`.

La base de datos `kutt_db` y sus tablas se crean automáticamente al arrancar.

## Cómo correrlo local

1. Enciende MySQL (XAMPP, por ejemplo).
2. Abre esta carpeta en una terminal.
3. Ejecuta:
   ```
   mvn spring-boot:run
   ```
4. La API queda disponible en `http://localhost:8080/api`.

Al arrancar por primera vez se cargan datos de ejemplo: clientes, productos, adicionales,
métodos de pago, la configuración del servicio base y dos cuentas de prueba.

## Roles

- **CLIENTE** — se registra con perfil completo (apellidos, teléfono, departamento, ciudad, género, fecha de nacimiento, dirección), agenda citas, compra productos, paga.
- **ADMIN_NEGOCIO** — dueño/administrador de un negocio (barbería, salón de uñas, estética, etc.): registra su negocio (nombre, rubro, teléfono fijo, ubicación, logo, portada), acepta o rechaza citas, configura servicio base, adicionales, productos, métodos de pago y personal, ve reportes.

## Cuentas de prueba

- **Admin de negocio:** `emilker@barbershop.com` / `emilker123`
- **Cliente:** `juan@correo.com` / `cliente123`

## Endpoints principales

| Método | Endpoint | Descripción |
|---|---|---|
| POST | /api/auth/registro | Crear cuenta (rol CLIENTE con perfil completo, o ADMIN_NEGOCIO con datos del negocio) |
| POST | /api/auth/login | Iniciar sesión, devuelve JWT |
| GET | /api/negocios | Lista los negocios registrados en la plataforma |
| GET | /api/clientes | Lista clientes |
| PUT | /api/clientes/{id} | Editar cliente (Super Admin) |
| GET | /api/citas | Lista citas |
| POST | /api/citas | Agendar cita (servicio base + adicionales elegidos) |
| PUT | /api/citas/{id}/confirmar | Aceptar cita (Super Admin) |
| PUT | /api/citas/{id}/rechazar | Rechazar cita (Super Admin) |
| PUT | /api/citas/{id}/cancelar | Cancelar cita |
| PUT | /api/citas/{id}/reprogramar | Reprogramar cita |
| PUT | /api/citas/{id}/finalizar | Marcar cita como atendida (Super Admin) |
| POST | /api/citas/{id}/pagar | Generar el pago (QR digital o ticket QR en efectivo) |
| GET | /api/productos | Lista el inventario |
| POST | /api/productos | Agregar producto (Super Admin) |
| POST | /api/ventas/compra | Comprar productos de forma independiente (cliente) |
| POST | /api/ventas/{id}/pagar | Pagar una compra de productos |
| GET | /api/adicionales | Lista adicionales (barba, cejas, tinte, etc.) |
| POST | /api/adicionales | Crear adicional (Super Admin) |
| GET | /api/barberos | Lista los barberos en servicio (con su foto) |
| POST | /api/barberos | Agregar barbero, con nombre y foto (Super Admin) |
| GET | /api/metodos-pago | Lista métodos de pago configurados |
| POST | /api/metodos-pago | Agregar método de pago (Super Admin) |
| GET | /api/configuracion | Ver servicio base y notificaciones |
| PUT | /api/configuracion | Editar servicio base y destino de notificaciones (Super Admin) |
| GET | /api/ventas | Lista ventas |

## Flujo de pago

Al agendar una cita o comprar productos, el pago no es obligatorio de inmediato. Cuando el
cliente decide pagar (`POST /api/citas/{id}/pagar` o `POST /api/ventas/{id}/pagar`):

- Si el método elegido es de tipo `DIGITAL` (Bancolombia, Nequi, etc.), se marca como **Pagado**
  y se genera un QR de pago (visual por ahora, sin conexión real a la pasarela).
- Si el método es `EFECTIVO`, queda **Pendiente** y se genera un ticket QR de reserva, para
  cobrarse en persona el día de la cita.

## Próximos pasos

- Reemplazar el CORS abierto a `localhost:4200` por configuración de producción.
- Integrar Firebase Authentication real para el login con Google/Facebook.
- Conectar los métodos de pago digitales a una pasarela real.
