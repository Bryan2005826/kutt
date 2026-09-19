package com.jostech.emilker.repository;

import com.jostech.emilker.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByEstadoAndRecordatorioEnviadoFalse(String estado);
}
