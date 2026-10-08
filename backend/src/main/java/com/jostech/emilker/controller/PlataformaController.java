package com.jostech.emilker.controller;

import com.jostech.emilker.model.Negocio;
import com.jostech.emilker.repository.CitaRepository;
import com.jostech.emilker.repository.ClienteRepository;
import com.jostech.emilker.repository.NegocioRepository;
import com.jostech.emilker.repository.UsuarioRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Este controlador es exclusivo del rol SUPER_ADMIN: el dueno de la plataforma Kutt,
// no de un negocio en particular. Da una foto general de toda la plataforma.
@RestController
@RequestMapping("/api/plataforma")
public class PlataformaController {

    private final NegocioRepository negocioRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final CitaRepository citaRepository;

    public PlataformaController(NegocioRepository negocioRepository, UsuarioRepository usuarioRepository,
                                 ClienteRepository clienteRepository, CitaRepository citaRepository) {
        this.negocioRepository = negocioRepository;
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.citaRepository = citaRepository;
    }

    @GetMapping("/resumen")
    public Map<String, Object> resumen() {
        List<Negocio> negocios = negocioRepository.findAll();

        Map<String, Long> porRubro = new HashMap<>();
        long verificados = 0;
        for (Negocio n : negocios) {
            String rubro = n.getRubro() != null ? n.getRubro() : "OTRO";
            porRubro.merge(rubro, 1L, Long::sum);
            if (n.isVerificado()) verificados++;
        }

        Map<String, Object> resumen = new HashMap<>();
        resumen.put("totalNegocios", negocios.size());
        resumen.put("negociosVerificados", verificados);
        resumen.put("negociosPorRubro", porRubro);
        resumen.put("totalClientes", clienteRepository.count());
        resumen.put("totalCitas", citaRepository.count());
        resumen.put("negocios", negocios);
        return resumen;
    }
}
