package com.jostech.emilker.repository;

import com.jostech.emilker.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByNegocioId(Long negocioId);
}
