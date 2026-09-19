package com.jostech.emilker.repository;

import com.jostech.emilker.model.Negocio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NegocioRepository extends JpaRepository<Negocio, Long> {
    Optional<Negocio> findByAdminCorreo(String adminCorreo);
}
