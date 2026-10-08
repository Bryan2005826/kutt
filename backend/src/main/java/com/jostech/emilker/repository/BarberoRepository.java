package com.jostech.emilker.repository;

import com.jostech.emilker.model.Barbero;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BarberoRepository extends JpaRepository<Barbero, Long> {
    List<Barbero> findByNegocioId(Long negocioId);
}
