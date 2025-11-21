package com.usuario.sistemaBD.infrastructure.jpa.repository;

import com.usuario.sistemaBD.infrastructure.jpa.entity.Plano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlanoRepository extends JpaRepository<Plano, Integer> {
    List<Plano> findByAtivoTrue();
    Optional<Plano> findByTipo(Plano.TipoPlano tipo);//
}