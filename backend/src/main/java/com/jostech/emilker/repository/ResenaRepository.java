package com.jostech.emilker.repository;

import com.jostech.emilker.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResenaRepository extends JpaRepository<Resena, Long> {
    List<Resena> findByNegocioId(Long negocioId);
}
