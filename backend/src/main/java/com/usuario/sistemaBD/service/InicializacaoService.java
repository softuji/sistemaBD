package com.usuario.sistemaBD.service;

import com.usuario.sistemaBD.infrastructure.jpa.entity.Usuario;
import com.usuario.sistemaBD.infrastructure.jpa.repository.UsuarioRepository;
import com.usuario.sistemaBD.infrastructure.mongodb.document.AvaliacaoFisica;
import com.usuario.sistemaBD.infrastructure.mongodb.document.PlanoTreino;
import com.usuario.sistemaBD.infrastructure.mongodb.repository.AvaliacaoFisicaRepository;
import com.usuario.sistemaBD.infrastructure.mongodb.repository.PlanoTreinoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;

@RequiredArgsConstructor
@Service
public class InicializacaoService {

    private final UsuarioRepository usuarioRepository;
    private final AvaliacaoFisicaRepository avaliacaoFisicaRepository;
    private final PlanoTreinoRepository planoTreinoRepository;
    private final AutorizacaoService autorizacaoService;

    //CRIAR DOCUMENTOS MONGODB
    public void criarDocumentosMongoDB(Integer usuarioId) {
        // Verifica se usuário existe
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Cria avaliação física vazia
        AvaliacaoFisica avaliacao = AvaliacaoFisica.builder()
                .usuarioId(usuarioId)
                .medidas(new ArrayList<>())
                .estatisticas(AvaliacaoFisica.Estatisticas.builder().build())
                .build();
        avaliacaoFisicaRepository.save(avaliacao);

        // Cria plano de treino vazio
        PlanoTreino treino = PlanoTreino.builder()
                .usuarioId(usuarioId)
                .nome("Treino Inicial")
                .objetivo("Condicionamento Básico")
                .exercicios(new ArrayList<>())
                .ativo(true)
                .dataCriacao(LocalDate.now())
                .build();
        planoTreinoRepository.save(treino);
    }

    //VERIFICAR SE DOCUMENTOS EXISTEM
    public boolean verificarDocumentosExistem(Integer usuarioId) {
        boolean temAvaliacao = avaliacaoFisicaRepository.findByUsuarioId(usuarioId).isPresent();
        boolean temTreino = planoTreinoRepository.findByUsuarioId(usuarioId).isPresent();

        return temAvaliacao && temTreino;
    }

    //REINICIALIZAR DOCUMENTOS (APENAS ADMIN)
    public void reinicializarDocumentos(Integer usuarioId) {
        // Apenas admin pode reinicializar
        autorizacaoService.validarAcessoAdmin(usuarioId);

        // Remove documentos existentes
        avaliacaoFisicaRepository.findByUsuarioId(usuarioId)
                .ifPresent(avaliacaoFisicaRepository::delete);
        planoTreinoRepository.findByUsuarioId(usuarioId)
                .ifPresent(planoTreinoRepository::delete);

        // Cria novos documentos
        criarDocumentosMongoDB(usuarioId);
    }
}