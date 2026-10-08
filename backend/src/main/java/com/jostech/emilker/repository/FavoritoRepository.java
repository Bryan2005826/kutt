package com.jostech.emilker.repository;

import com.jostech.emilker.model.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    List<Favorito> findByClienteCorreo(String clienteCorreo);
    Optional<Favorito> findByClienteCorreoAndNegocioId(String clienteCorreo, Long negocioId);
}
