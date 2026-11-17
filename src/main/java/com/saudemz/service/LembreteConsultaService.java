
package com.saudemz.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saudemz.model.Consulta;
import com.saudemz.model.Usuario;
import com.saudemz.repository.ConsultaRepository;

@Service
public class LembreteConsultaService {

    private final ConsultaRepository consultaRepository;
    private final EmailService emailService;

    public LembreteConsultaService(
            ConsultaRepository consultaRepository,
            EmailService emailService) {

        this.consultaRepository = consultaRepository;
        this.emailService = emailService;
    }

    // VERIFICAR CONSULTAS A CADA 60 SEGUNDOS
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void enviarLembretes() {

        LocalDateTime agora = LocalDateTime.now();

        // Procurar consultas que estão entre 24h e 24h01 à frente
        LocalDateTime inicio = agora.plusHours(24);
        LocalDateTime fim = inicio.plusMinutes(1);

        List<Consulta> consultas =
                consultaRepository.buscarConsultasParaLembrete(
                        inicio, fim);

        for (Consulta consulta : consultas) {

            try {
                Usuario usuario = consulta.getPaciente().getUsuario();

                if (usuario == null
                        || usuario.getEmail() == null
                        || usuario.getEmail().isBlank()) {
                    continue;
                }

                String assunto = "Lembrete de consulta - Saúde+ MZ";

                String mensagem = """
                        Olá, %s!

                        Este é um lembrete da sua consulta médica.

                        Data e hora: %s
                        Médico: %s
                        Especialidade: %s

                        Aguardamos a sua presença.

                        Saúde+ MZ
                        """.formatted(
                        consulta.getPaciente().getNome(),
                        consulta.getDataHora().format(
                                DateTimeFormatter.ofPattern(
                                        "dd/MM/yyyy HH:mm")),
                        consulta.getMedico().getNome(),
                        consulta.getMedico().getEspecialidade()
                );

                emailService.enviarEmail(
                        usuario.getEmail(),
                        assunto,
                        mensagem
                );

                consulta.setLembreteEnviado(true);
                consultaRepository.save(consulta);

                System.out.println(
                        "Lembrete enviado para a consulta "
                        + consulta.getId());

            } catch (Exception e) {
                System.err.println(
                        "Erro ao enviar lembrete da consulta "
                        + consulta.getId() + ": " + e.getMessage());
            }
        }
    }
}
