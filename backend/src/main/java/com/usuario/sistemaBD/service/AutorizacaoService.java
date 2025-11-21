package com.usuario.sistemaBD.service;

import com.usuario.sistemaBD.infrastructure.jpa.entity.Usuario;
import com.usuario.sistemaBD.infrastructure.jpa.repository.UsuarioRepository;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Service;

@Data
@Builder
@Service
public class AutorizacaoService {

    private final UsuarioRepository usuarioRepository;

    public AutorizacaoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    //VALIDAR ACESSO INSTRUTOR/ADMIN
    public void validarAcessoInstrutor(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (usuario.getTipo() != Usuario.TipoUsuario.INSTRUTOR &&
                usuario.getTipo() != Usuario.TipoUsuario.ADMIN) {
            throw new RuntimeException("Acesso permitido apenas para instrutores e administradores");
        }
    }

    //VALIDAR ACESSO APENAS ADMIN
    public void validarAcessoAdmin(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (usuario.getTipo() != Usuario.TipoUsuario.ADMIN) {
            throw new RuntimeException("Acesso permitido apenas para administradores");
        }
    }

    //OBTER TIPO DO USUÁRIO
    public String obterTipoUsuario(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return usuario.getTipo().name();
    }

}