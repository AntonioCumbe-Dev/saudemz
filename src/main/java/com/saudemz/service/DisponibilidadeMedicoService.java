package com.saudemz.service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saudemz.model.DisponibilidadeMedico;
import com.saudemz.model.Medico;
import com.saudemz.repository.DisponibilidadeMedicoRepository;
import com.saudemz.repository.MedicoRepository;

@Service
public class DisponibilidadeMedicoService {

    private final DisponibilidadeMedicoRepository disponibilidadeRepository;
    private final MedicoRepository medicoRepository;

    public DisponibilidadeMedicoService(
            DisponibilidadeMedicoRepository disponibilidadeRepository,
            MedicoRepository medicoRepository) {

        this.disponibilidadeRepository = disponibilidadeRepository;
        this.medicoRepository = medicoRepository;
    }

    @Transactional
    public DisponibilidadeMedico registar(
            Long medicoId, DisponibilidadeMedico disponibilidade) {

        if (disponibilidade == null) {
            throw new IllegalArgumentException("Dados de disponibilidade inválidos.");
        }

        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Médico não encontrado."));

        if (disponibilidade.getDiaSemana() == null
                || disponibilidade.getHoraInicio() == null
                || disponibilidade.getHoraFim() == null) {
            throw new IllegalArgumentException(
                    "O dia da semana e os horários são obrigatórios.");
        }

        LocalTime inicio = disponibilidade.getHoraInicio();
        LocalTime fim = disponibilidade.getHoraFim();

        if (!inicio.isBefore(fim)) {
            throw new IllegalArgumentException(
                    "A hora de início deve ser anterior à hora de fim.");
        }

        if (inicio.isBefore(LocalTime.of(6, 0))
                || fim.isAfter(LocalTime.of(20, 0))) {
            throw new IllegalArgumentException(
                    "O horário deve estar entre 06:00 e 20:00.");
        }

        List<DisponibilidadeMedico> existentes =
                disponibilidadeRepository
                        .findByMedicoIdAndDiaSemanaAndDisponivelTrue(
                                medicoId, disponibilidade.getDiaSemana());

        for (DisponibilidadeMedico existente : existentes) {

            boolean sobreposicao =
                    inicio.isBefore(existente.getHoraFim())
                    && fim.isAfter(existente.getHoraInicio());

            if (sobreposicao) {
                throw new IllegalArgumentException(
                        "Este horário sobrepõe-se a uma disponibilidade existente.");
            }
        }

        disponibilidade.setMedico(medico);
        disponibilidade.setDisponivel(true);

        return disponibilidadeRepository.save(disponibilidade);
    }

    @Transactional(readOnly = true)
    public List<DisponibilidadeMedico> listarPorMedico(Long medicoId) {

        if (!medicoRepository.existsById(medicoId)) {
            throw new IllegalArgumentException("Médico não encontrado.");
        }

        return disponibilidadeRepository.findByMedicoId(medicoId);
    }

    @Transactional(readOnly = true)
    public List<DisponibilidadeMedico> listarPorDia(DayOfWeek dia) {

        if (dia == null) {
            throw new IllegalArgumentException(
                    "O dia da semana é obrigatório.");
        }

        return disponibilidadeRepository.findByDiaSemanaAndDisponivelTrue(dia);
    }

    @Transactional
    public DisponibilidadeMedico desactivar(Long id) {

        DisponibilidadeMedico disponibilidade =
                disponibilidadeRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Disponibilidade não encontrada."));

        disponibilidade.setDisponivel(false);

        return disponibilidadeRepository.save(disponibilidade);
    }
}