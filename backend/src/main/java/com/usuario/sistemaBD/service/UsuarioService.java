package com.usuario.sistemaBD.service;

import com.usuario.sistemaBD.infrastructure.jpa.entity.Plano;
import com.usuario.sistemaBD.infrastructure.jpa.repository.PlanoRepository;
import com.usuario.sistemaBD.infrastructure.mongodb.document.PlanoTreino;
import com.usuario.sistemaBD.infrastructure.mongodb.repository.PlanoTreinoRepository;
import com.usuario.sistemaBD.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.usuario.sistemaBD.infrastructure.jpa.entity.Usuario;
import com.usuario.sistemaBD.infrastructure.jpa.repository.UsuarioRepository;
import com.usuario.sistemaBD.infrastructure.mongodb.document.AvaliacaoFisica;
import com.usuario.sistemaBD.infrastructure.mongodb.repository.AvaliacaoFisicaRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final AvaliacaoFisicaRepository avaliacaoFisicaRepository;
    private final PlanoTreinoRepository planoTreinoRepository;
    private final PlanoRepository planoRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario cadastrarUsuario(String nome, String email, String senha, String confirmarSenha, String tipo) {
        if (!senha.equals(confirmarSenha)) {
            throw new RuntimeException("Senhas não conferem");
        }

        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        Usuario.TipoUsuario tipoUsuario;
        try {
            tipoUsuario = Usuario.TipoUsuario.valueOf(tipo.toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException("Tipo deve ser: ALUNO, INSTRUTOR ou ADMIN");
        }

        String senhaHash = passwordEncoder.encode(senha);

        Usuario usuario = Usuario.builder()
                .nome(nome)
                .email(email)
                .senha(senhaHash)
                .tipo(tipoUsuario)
                .ativo(true)
                .dataCadastro(LocalDate.now())
                .build();

        return usuarioRepository.save(usuario);
    }

    public Usuario fazerLogin(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new RuntimeException("Senha incorreta");
        }

        if (!usuario.isAtivo()) {
            throw new RuntimeException("Usuário inativo");
        }

        return usuario;
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("Email não encontrado")
        );
    }

    public void deletarUsuarioPorEmail(String email) {
        usuarioRepository.deleteByEmail(email);
    }

    public void atualizarUsuarioPorId(Integer id, Usuario usuario) {
        Usuario usuarioEntity = usuarioRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Usuário não encontrado"));

        Usuario usuarioAtualizado = Usuario.builder()
                .id(usuarioEntity.getId())
                .email(usuario.getEmail() != null ? usuario.getEmail() : usuarioEntity.getEmail())
                .nome(usuario.getNome() != null ? usuario.getNome() : usuarioEntity.getNome())
                .senha(usuario.getSenha() != null ? usuario.getSenha() : usuarioEntity.getSenha())
                .telefone(usuario.getTelefone() != null ? usuario.getTelefone() : usuarioEntity.getTelefone())
                .dataNascimento(usuario.getDataNascimento() != null ? usuario.getDataNascimento() : usuarioEntity.getDataNascimento())
                .tipo(usuario.getTipo() != null ? usuario.getTipo() : usuarioEntity.getTipo())
                .dataCadastro(usuarioEntity.getDataCadastro())
                .ativo(usuarioEntity.isAtivo())
                .plano(usuario.getPlano() != null ? usuario.getPlano() : usuarioEntity.getPlano())
                .mongoAvaliacoesId(usuario.getMongoAvaliacoesId() != null ? usuario.getMongoAvaliacoesId() : usuarioEntity.getMongoAvaliacoesId())
                .mongoTreinosId(usuario.getMongoTreinosId() != null ? usuario.getMongoTreinosId() : usuarioEntity.getMongoTreinosId())
                .build();

        usuarioRepository.saveAndFlush(usuarioAtualizado);
    }

    public List<Usuario> listarTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    public Plano criarPlanoBasico() {
        return planoRepository.save(Plano.builder()
                .tipo(Plano.TipoPlano.BASICO)
                .valorMensal(89.90)
                .modalidadesPermitidas(List.of(
                        Plano.Modalidade.MUSCULACAO
                ))
                .ativo(true)
                .build());
    }

    public Plano criarPlanoPremium() {
        return planoRepository.save(Plano.builder()
                .tipo(Plano.TipoPlano.PREMIUM)
                .valorMensal(149.90)
                .modalidadesPermitidas(List.of(
                        Plano.Modalidade.MUSCULACAO,
                        Plano.Modalidade.YOGA,
                        Plano.Modalidade.PILATES,
                        Plano.Modalidade.CROSSFIT,
                        Plano.Modalidade.DANCA,
                        Plano.Modalidade.LUTAS
                ))
                .ativo(true)
                .build());
    }

    public Plano criarPlanoTop() {
        return planoRepository.save(Plano.builder()
                .tipo(Plano.TipoPlano.TOP)
                .valorMensal(199.90)
                .modalidadesPermitidas(List.of(Plano.Modalidade.values()))
                .ativo(true)
                .build());
    }

    public List<Plano> listarPlanosAtivos() {
        return planoRepository.findByAtivoTrue();
    }

    public Plano buscarPlanoPorTipo(Plano.TipoPlano tipo) {
        return planoRepository.findByTipo(tipo)
                .orElseThrow(() -> new RuntimeException("Plano não encontrado"));
    }

    public void adicionarAvaliacaoFisica(Integer usuarioId, Double peso, Double altura, String observacoes) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        AvaliacaoFisica avaliacao = avaliacaoFisicaRepository.findByUsuarioId(usuarioId)
                .orElse(AvaliacaoFisica.builder()
                        .usuarioId(usuarioId)
                        .medidas(new ArrayList<>())
                        .estatisticas(AvaliacaoFisica.Estatisticas.builder().build())
                        .build());

        AvaliacaoFisica.Medida novaMedida = AvaliacaoFisica.Medida.builder()
                .data(LocalDate.now())
                .peso(peso)
                .altura(altura)
                .imc(calcularIMC(peso, altura))
                .frequenciaSemanal(0) // Valor padrão
                .forcaMedia(0.0) // Valor padrão
                .resistencia(0.0) // Valor padrão
                .observacoes(observacoes)
                .build();

        avaliacao.getMedidas().add(novaMedida);

        // SALVA e obtém o ID gerado
        AvaliacaoFisica avaliacaoSalva = avaliacaoFisicaRepository.save(avaliacao);

        // ATUALIZA o usuário com o ID do MongoDB
        usuario.setMongoAvaliacoesId(avaliacaoSalva.getId());
        usuarioRepository.save(usuario);
    }

    public List<AvaliacaoFisica.Medida> buscarHistoricoAvaliacoes(Integer usuarioId) {
        AvaliacaoFisica avaliacao = avaliacaoFisicaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada"));

        return avaliacao.getMedidas();
    }

    public void registrarProgresso(Integer usuarioId, Double peso, Double altura,
                                   Integer frequenciaSemanal, Double forcaMedia,
                                   Double resistencia, String observacoes) {

        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        AvaliacaoFisica avaliacao = avaliacaoFisicaRepository.findByUsuarioId(usuarioId)
                .orElse(AvaliacaoFisica.builder()
                        .usuarioId(usuarioId)
                        .medidas(new ArrayList<>())
                        .estatisticas(AvaliacaoFisica.Estatisticas.builder().build())
                        .build());

        AvaliacaoFisica.Medida registro = AvaliacaoFisica.Medida.builder()
                .data(LocalDate.now())
                .peso(peso)
                .altura(altura)
                .imc(calcularIMC(peso, altura))
                .frequenciaSemanal(frequenciaSemanal)
                .forcaMedia(forcaMedia)
                .resistencia(resistencia)
                .observacoes(observacoes)
                .build();

        avaliacao.getMedidas().add(registro);

        atualizarEstatisticas(avaliacao);

        avaliacaoFisicaRepository.save(avaliacao);
    }

    public Map<String, Object> getDadosGraficos(Integer usuarioId) {
        AvaliacaoFisica avaliacao = avaliacaoFisicaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Avaliação física não encontrada"));


        Map<String, Object> dadosGraficos = new HashMap<>();

        if (avaliacao.getMedidas() == null || avaliacao.getMedidas().isEmpty()) {
            dadosGraficos.put("peso_evolucao", new HashMap<>());
            dadosGraficos.put("imc_evolucao", new HashMap<>());
            dadosGraficos.put("forca_evolucao", new HashMap<>());
            dadosGraficos.put("resistencia_evolucao", new HashMap<>());
            dadosGraficos.put("estatisticas", new HashMap<>());
            return dadosGraficos;
        }

        dadosGraficos.put("peso_evolucao", avaliacao.getMedidas().stream()
                .collect(Collectors.toMap(
                        AvaliacaoFisica.Medida::getData,
                        AvaliacaoFisica.Medida::getPeso
                )));

        // Gráfico de IMC
        dadosGraficos.put("imc_evolucao", avaliacao.getMedidas().stream()
                .collect(Collectors.toMap(
                        AvaliacaoFisica.Medida::getData,
                        AvaliacaoFisica.Medida::getImc
                )));

        // Gráfico de Força
        dadosGraficos.put("forca_evolucao", avaliacao.getMedidas().stream()
                .collect(Collectors.toMap(
                        AvaliacaoFisica.Medida::getData,
                        AvaliacaoFisica.Medida::getForcaMedia
                )));

        // Gráfico de Resistência
        dadosGraficos.put("resistencia_evolucao", avaliacao.getMedidas().stream()
                .collect(Collectors.toMap(
                        AvaliacaoFisica.Medida::getData,
                        AvaliacaoFisica.Medida::getResistencia
                )));

        // Estatísticas resumidas
        dadosGraficos.put("estatisticas", avaliacao.getEstatisticas());

        return dadosGraficos;
    }

    private void atualizarEstatisticas(AvaliacaoFisica avaliacao) {
        if (avaliacao.getMedidas().isEmpty()) return;

        List<AvaliacaoFisica.Medida> medidas = avaliacao.getMedidas();
        AvaliacaoFisica.Medida primeiro = medidas.get(0);
        AvaliacaoFisica.Medida ultimo = medidas.get(medidas.size() - 1);

        AvaliacaoFisica.Estatisticas stats = AvaliacaoFisica.Estatisticas.builder()
                .pesoInicial(primeiro.getPeso())
                .pesoAtual(ultimo.getPeso())
                .variacaoPeso(ultimo.getPeso() - primeiro.getPeso())
                .imcInicial(primeiro.getImc())
                .imcAtual(ultimo.getImc())
                .totalSessoes(medidas.size())
                .mediaFrequenciaSemanal(medidas.stream()
                        .mapToInt(AvaliacaoFisica.Medida::getFrequenciaSemanal)
                        .average().orElse(0.0))
                .build();

        avaliacao.setEstatisticas(stats);
    }

    public void criarPlanoTreino(Integer usuarioId, String nome, String objetivo) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        PlanoTreino planoTreino = PlanoTreino.builder()
                .usuarioId(usuarioId)
                .nome(nome)
                .objetivo(objetivo)
                .exercicios(new ArrayList<>())
                .ativo(true)
                .dataCriacao(LocalDate.now())
                .build();

        // SALVA e obtém o ID gerado
        PlanoTreino treinoSalvo = planoTreinoRepository.save(planoTreino);

        // ATUALIZA o usuário com o ID do MongoDB
        usuario.setMongoTreinosId(treinoSalvo.getId());
        usuarioRepository.save(usuario);
    }

    public void adicionarExercicioPlanoTreino(Integer usuarioId, String nome, Integer series,
                                              String repeticoes, String carga, String observacoes) {
        PlanoTreino planoTreino = planoTreinoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Plano de treino não encontrado"));

        PlanoTreino.Exercicio exercicio = PlanoTreino.Exercicio.builder()
                .nome(nome)
                .series(series)
                .repeticoes(repeticoes)
                .carga(carga)
                .observacoes(observacoes)
                .build();

        planoTreino.getExercicios().add(exercicio);
        planoTreinoRepository.save(planoTreino);
    }

    public PlanoTreino buscarPlanoTreino(Integer usuarioId) {
        return planoTreinoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Plano de treino não encontrado"));
    }

    public List<PlanoTreino> listarPlanosTreinoAtivos() {
        return planoTreinoRepository.findByAtivoTrue();
    }

    private Double calcularIMC(Double peso, Double altura) {
        return peso / (altura * altura);
    }
}