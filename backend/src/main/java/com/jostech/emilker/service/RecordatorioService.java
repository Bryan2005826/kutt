package com.jostech.emilker.service;

import com.jostech.emilker.model.Cita;
import com.jostech.emilker.repository.CitaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordatorioService {

    private static final Logger log = LoggerFactory.getLogger(RecordatorioService.class);

    private final CitaRepository citaRepository;
    // ObjectProvider: si no hay spring.mail.host configurado, no existe un bean JavaMailSender
    // y la app debe seguir arrancando igual (el recordatorio simplemente queda solo en el log).
    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    public RecordatorioService(CitaRepository citaRepository, ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.citaRepository = citaRepository;
        this.mailSenderProvider = mailSenderProvider;
    }

    // Revisa cada 5 minutos las citas confirmadas que todavía no tienen recordatorio enviado
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void enviarRecordatoriosPendientes() {
        List<Cita> pendientes = citaRepository.findByEstadoAndRecordatorioEnviadoFalse("Confirmada");
        if (pendientes.isEmpty()) return;

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();

        for (Cita cita : pendientes) {
            if (mailSender != null) {
                try {
                    enviarCorreo(mailSender, cita);
                    log.info("Recordatorio enviado por correo a {} para su cita del {} a las {}",
                            cita.getCliente(), cita.getFecha(), cita.getHora());
                } catch (Exception errorEnvio) {
                    log.warn("No se pudo enviar el recordatorio de la cita {}: {}", cita.getId(), errorEnvio.getMessage());
                }
            } else {
                // CU-09, flujo alternativo: sin notificaciones configuradas, se deja constancia igual.
                log.info("[Recordatorio] {} tiene cita el {} a las {} (correo no configurado en application.properties)",
                        cita.getCliente(), cita.getFecha(), cita.getHora());
            }

            cita.setRecordatorioEnviado(true);
            citaRepository.save(cita);
        }
    }

    private void enviarCorreo(JavaMailSender mailSender, Cita cita) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setSubject("Recordatorio de tu cita en Emilker Barber Shop");
        mensaje.setText(
                "Hola " + cita.getCliente() + ",\n\n" +
                "Te recordamos tu cita:\n" +
                "Servicio: " + cita.getServicio() + "\n" +
                "Fecha: " + cita.getFecha() + " a las " + cita.getHora() + "\n\n" +
                "Te esperamos en Emilker Barber Shop."
        );
        mailSender.send(mensaje);
    }
}
