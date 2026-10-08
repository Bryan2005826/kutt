package com.jostech.emilker.repository;

import com.jostech.emilker.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);

    // CU: recuperar contraseña — buscar al usuario dueño de un token de restablecimiento vigente
    Optional<Usuario> findByResetToken(String resetToken);
}
