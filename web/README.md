# Kutt — Frontend (Angular)

Landing page + flujo de cliente + panel de administrador de negocio del sistema, en Angular 17.
Caso de implementación: Emilker Barber Shop.

## Cómo correrlo local

1. Instala Node.js 18 o superior.
2. Abre esta carpeta en una terminal.
3. Instala dependencias:
   ```
   npm install
   ```
4. Corre el servidor de desarrollo:
   ```
   npm start
   ```
5. Abre `http://localhost:4200`.

Necesitas el backend (`kutt-backend`) corriendo en paralelo en el puerto 8080, con MySQL encendido.

## Cuentas de prueba

- **Admin de negocio:** `emilker@barbershop.com` / `emilker123`
- **Cliente:** `juan@correo.com` / `cliente123`

## Rutas

**Cliente:**
- `/` — Landing page
- `/login` — Login único (mismo formulario para cliente y admin de negocio)
- `/registro/usuario` — Crear cuenta de cliente (perfil completo: apellidos, teléfono, ubicación, género, fecha de nacimiento)
- `/registro/negocio` — Registrar un negocio nuevo (nombre, rubro, teléfono fijo, ubicación, logo, portada)
- `/agendar` — Agendar cita: elige barbero (con foto) → servicio base + adicionales → fecha/hora/celular → confirmar
- `/pagar/cita/:id` — Elegir método de pago (QR digital o ticket en efectivo)
- `/mis-citas` — Ver, pagar, reprogramar y cancelar mis citas
- `/productos` — Catálogo con carrito, compra independiente de productos
- `/pagar/venta/:id` — Pago de una compra de productos

**Admin de negocio** (login unificado en `/login`; el registro de un nuevo negocio se hace en `/registro/negocio`):
- `/dashboard` — Resumen general
- `/dashboard/agenda` — Aceptar, rechazar y finalizar citas; ver celular del cliente
- `/dashboard/clientes` — Clientes registrados, editables
- `/dashboard/inventario` — Productos y existencias
- `/dashboard/ventas` — Historial de ventas + registrar venta manual
- `/dashboard/reportes` — Estadísticas
- `/dashboard/ajustes` — Servicio base, adicionales, métodos de pago, notificaciones, barberos en servicio (con foto)

## Flujo de pago

Al agendar una cita o comprar productos, el pago se resuelve después, desde "Mis citas" o justo
tras confirmar la compra:

- Método **digital** (Bancolombia, Nequi, etc.) → se genera un QR de pago (visual, no conecta aún
  con una pasarela real) y queda marcado como **Pagado**.
- Método **efectivo** → se genera un **ticket QR de reserva**, y el pago queda **Pendiente** hasta
  que el administrador lo cobre en persona el día de la cita.

## Cómo está conectado

Todo corre sobre `src/app/services/data.service.ts` (datos: citas, productos, adicionales,
métodos de pago, configuración) y `src/app/services/auth.service.ts` (sesión JWT), hablando con
la API REST del backend.

## Pendiente

- Firebase Authentication real para Google/Facebook.
- Conexión real de los métodos de pago digitales a una pasarela.
- Editar/eliminar productos del inventario ya creados.
- Agenda diferenciada por barbero (hoy el modelo asume un único punto de atención por negocio).
