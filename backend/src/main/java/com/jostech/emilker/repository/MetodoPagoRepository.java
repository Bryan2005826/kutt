package com.jostech.emilker.repository;

import com.jostech.emilker.model.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {
    List<MetodoPago> findByNegocioId(Long negocioId);
}
