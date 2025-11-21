package com.usuario.sistemaBD.infrastructure.mongodb.repository;

import com.usuario.sistemaBD.infrastructure.mongodb.document.AvaliacaoFisica;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvaliacaoFisicaRepository extends MongoRepository<AvaliacaoFisica, String> {
    Optional<AvaliacaoFisica> findByUsuarioId(Integer usuarioId);
}