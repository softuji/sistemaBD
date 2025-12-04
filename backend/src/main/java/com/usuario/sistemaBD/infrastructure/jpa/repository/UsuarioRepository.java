package com.usuario.sistemaBD.infrastructure.jpa.repository;

import com.usuario.sistemaBD.infrastructure.jpa.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);

    int countByTipo(Usuario.TipoUsuario tipo);


    @Transactional
    void deleteByEmail(String email);
}