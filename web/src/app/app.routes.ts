import { Routes } from '@angular/router';
import { LandingComponent } from './pages/landing/landing.component';
import { LoginComponent } from './pages/login/login.component';
import { RegistroUsuarioComponent } from './pages/registro-usuario/registro-usuario.component';
import { RegistroNegocioComponent } from './pages/registro-negocio/registro-negocio.component';
import { BookingComponent } from './pages/booking/booking.component';
import { MyAppointmentsComponent } from './pages/my-appointments/my-appointments.component';
import { ProductsComponent } from './pages/products/products.component';
import { PagoComponent } from './pages/pago/pago.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { DashboardHomeComponent } from './pages/dashboard/home/dashboard-home.component';
import { AgendaComponent } from './pages/dashboard/agenda/agenda.component';
import { ClientesComponent } from './pages/dashboard/clientes/clientes.component';
import { InventarioComponent } from './pages/dashboard/inventario/inventario.component';
import { VentasComponent } from './pages/dashboard/ventas/ventas.component';
import { ReportesComponent } from './pages/dashboard/reportes/reportes.component';
import { AjustesComponent } from './pages/dashboard/ajustes/ajustes.component';
import { clienteGuard, adminNegocioGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', component: LandingComponent },
  { path: 'login', component: LoginComponent },
  { path: 'registro/usuario', component: RegistroUsuarioComponent },
  { path: 'registro/negocio', component: RegistroNegocioComponent },
  { path: 'agendar', component: BookingComponent, canActivate: [clienteGuard] },
  { path: 'mis-citas', component: MyAppointmentsComponent, canActivate: [clienteGuard] },
  { path: 'productos', component: ProductsComponent, canActivate: [clienteGuard] },
  { path: 'pagar/:tipo/:id', component: PagoComponent, canActivate: [clienteGuard] },
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
