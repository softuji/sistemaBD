package com.usuario.sistemaBD.infrastructure.mongodb.repository;

import com.usuario.sistemaBD.infrastructure.mongodb.document.PlanoTreino;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlanoTreinoRepository extends MongoRepository<PlanoTreino, String> {
    Optional<PlanoTreino> findByUsuarioId(Integer usuarioId);
    List<PlanoTreino> findByAtivoTrue();

}