import { Component, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink, Router } from '@angular/router';
import { DataService } from '../../services/data.service';
import { AuthService } from '../../services/auth.service';
import { LoginModalComponent } from '../../components/login-modal/login-modal.component';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule, RouterLink, LoginModalComponent],
  templateUrl: './products.component.html',
  styleUrl: './products.component.css'
})
export class ProductsComponent {
  negocioId: number;
  carrito = signal<Record<number, number>>({});
  comprando = signal(false);
  mostrarLoginModal = signal(false);

  constructor(private route: ActivatedRoute, public data: DataService, public auth: AuthService, private router: Router) {
    this.negocioId = Number(this.route.snapshot.paramMap.get('id'));
    // Multi-tenant: solo los productos de ESTE negocio, no los de toda la plataforma
    this.data.cargarProductos(this.negocioId);
  }

  cantidadEnCarrito(productoId: number): number {
    return this.carrito()[productoId] ?? 0;
  }

  agregar(productoId: number) {
    this.carrito.update(c => ({ ...c, [productoId]: (c[productoId] ?? 0) + 1 }));
  }

  quitar(productoId: number) {
    this.carrito.update(c => {
      const actual = (c[productoId] ?? 0) - 1;
      const copia = { ...c };
      if (actual <= 0) delete copia[productoId]; else copia[productoId] = actual;
      return copia;
    });
  }

  itemsCarrito = computed(() => {
    const carrito = this.carrito();
    return this.data.productos()
      .filter(p => carrito[p.id] > 0)
      .map(p => ({ producto: p, cantidad: carrito[p.id] }));
  });

  totalCarrito = computed(() =>
    this.itemsCarrito().reduce((sum, it) => sum + it.producto.precio * it.cantidad, 0)
  );

  comprar() {
    // Igual que en agendar cita: se puede armar el carrito libremente, pero
    // para confirmar la compra sí se necesita sesión.
    if (!this.auth.usuario()) {
      this.mostrarLoginModal.set(true);
      return;
    }
    this.comprarDeVerdad();
  }

  loginExitoso() {
    this.mostrarLoginModal.set(false);
    this.comprarDeVerdad();
  }

  cerrarLoginModal() {
    this.mostrarLoginModal.set(false);
  }

  private comprarDeVerdad() {
    const usuario = this.auth.usuario();
    const nombreCliente = usuario ? usuario.nombre : 'Cliente';
    const items = this.itemsCarrito().map(it => ({ productoId: it.producto.id, cantidad: it.cantidad }));
    if (items.length === 0) return;

    this.comprando.set(true);
    this.data.comprarProductos(this.negocioId, nombreCliente, items, venta => {
      this.comprando.set(false);
      this.router.navigate(['/pagar/venta', venta.id]);
    });
  }

  formatPrecio(valor: number): string {
    return '$' + valor.toLocaleString('es-CO');
  }
}
