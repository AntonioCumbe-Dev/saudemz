package com.saudemz.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saudemz.model.Medico;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    Optional<Medico> findByUsuarioEmail(String email);

    Optional<Medico> findByUsuarioId(Long usuarioId);

}