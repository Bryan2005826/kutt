package com.jostech.emilker.repository;

import com.jostech.emilker.model.Adicional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdicionalRepository extends JpaRepository<Adicional, Long> {
    List<Adicional> findByNegocioId(Long negocioId);
}
