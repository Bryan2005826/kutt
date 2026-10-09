import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { RegistroUsuarioComponent } from './pages/registro-usuario/registro-usuario.component';
import { RegistroNegocioComponent } from './pages/registro-negocio/registro-negocio.component';
import { OlvidePasswordComponent } from './pages/olvide-password/olvide-password.component';
import { RestablecerPasswordComponent } from './pages/restablecer-password/restablecer-password.component';
import { BookingComponent } from './pages/booking/booking.component';
import { MyAppointmentsComponent } from './pages/my-appointments/my-appointments.component';
import { ProductsComponent } from './pages/products/products.component';
import { PagoComponent } from './pages/pago/pago.component';
import { TicketComponent } from './pages/ticket/ticket.component';
import { DescubrirComponent } from './pages/descubrir/descubrir.component';
import { NegocioPerfilComponent } from './pages/negocio-perfil/negocio-perfil.component';
import { FavoritosComponent } from './pages/favoritos/favoritos.component';
import { PerfilUsuarioComponent } from './pages/perfil-usuario/perfil-usuario.component';
import { PlataformaComponent } from './pages/plataforma/plataforma.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { DashboardHomeComponent } from './pages/dashboard/home/dashboard-home.component';
import { AgendaComponent } from './pages/dashboard/agenda/agenda.component';
import { ClientesComponent } from './pages/dashboard/clientes/clientes.component';
import { InventarioComponent } from './pages/dashboard/inventario/inventario.component';
import { VentasComponent } from './pages/dashboard/ventas/ventas.component';
import { ReportesComponent } from './pages/dashboard/reportes/reportes.component';
import { AjustesComponent } from './pages/dashboard/ajustes/ajustes.component';
import { clienteGuard, adminNegocioGuard, superAdminGuard } from './guards/auth.guard';

export const routes: Routes = [
  // Descubrir es la puerta de entrada publica: cualquiera puede "chismosear"
  // el catalogo de negocios sin iniciar sesion. Solo se pide login mas
  // adelante, justo en el momento de dar like, confirmar una cita o pagar.
  { path: '', redirectTo: 'descubrir', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'registro/usuario', component: RegistroUsuarioComponent },
  { path: 'registro/negocio', component: RegistroNegocioComponent },
  { path: 'olvide-password', component: OlvidePasswordComponent },
  { path: 'restablecer-password', component: RestablecerPasswordComponent },
  { path: 'descubrir', component: DescubrirComponent },
  { path: 'negocio/:id', component: NegocioPerfilComponent },
  // Multi-tenant: agendar y ver productos siempre es DENTRO de un negocio concreto
  { path: 'negocio/:id/agendar', component: BookingComponent },
  { path: 'negocio/:id/productos', component: ProductsComponent },
  { path: 'favoritos', component: FavoritosComponent, canActivate: [clienteGuard] },
  { path: 'perfil', component: PerfilUsuarioComponent, canActivate: [clienteGuard] },
  { path: 'mis-citas', component: MyAppointmentsComponent, canActivate: [clienteGuard] },
  { path: 'pagar/:tipo/:id', component: PagoComponent, canActivate: [clienteGuard] },
  // Pagina publica que se abre al escanear el QR de un pago/ticket (sin login)
  { path: 'ticket/:tipo/:id', component: TicketComponent },
  { path: 'plataforma', component: PlataformaComponent, canActivate: [superAdminGuard] },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [adminNegocioGuard],
    children: [
      { path: '', component: DashboardHomeComponent },
      { path: 'agenda', component: AgendaComponent },
      { path: 'clientes', component: ClientesComponent },
      { path: 'inventario', component: InventarioComponent },
      { path: 'ventas', component: VentasComponent },
      { path: 'reportes', component: ReportesComponent },
      { path: 'ajustes', component: AjustesComponent }
    ]
  },
  { path: '**', redirectTo: '' }
];
