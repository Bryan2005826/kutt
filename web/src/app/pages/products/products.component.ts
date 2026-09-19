import { Component, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { DataService } from '../../services/data.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './products.component.html',
  styleUrl: './products.component.css'
})
export class ProductsComponent {
  carrito = signal<Record<number, number>>({});
  comprando = signal(false);

  constructor(public data: DataService, public auth: AuthService, private router: Router) {}

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
    const usuario = this.auth.usuario();
    const nombreCliente = usuario ? usuario.nombre : 'Cliente';
    const items = this.itemsCarrito().map(it => ({ productoId: it.producto.id, cantidad: it.cantidad }));
    if (items.length === 0) return;

    this.comprando.set(true);
    this.data.comprarProductos(nombreCliente, items, venta => {
      this.comprando.set(false);
      this.router.navigate(['/pagar/venta', venta.id]);
    });
  }

  formatPrecio(valor: number): string {
    return '$' + valor.toLocaleString('es-CO');
  }
}
