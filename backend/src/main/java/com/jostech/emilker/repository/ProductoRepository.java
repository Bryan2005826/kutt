package com.jostech.emilker.repository;

import com.jostech.emilker.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByNegocioId(Long negocioId);
}
