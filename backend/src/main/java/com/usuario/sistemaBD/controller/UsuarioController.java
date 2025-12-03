package com.usuario.sistemaBD.controller;
import com.usuario.sistemaBD.infrastructure.jpa.entity.Plano;
import com.usuario.sistemaBD.infrastructure.jpa.entity.Usuario;
import com.usuario.sistemaBD.infrastructure.mongodb.document.AvaliacaoFisica;
import com.usuario.sistemaBD.infrastructure.mongodb.document.PlanoTreino;
import com.usuario.sistemaBD.service.AutorizacaoService;
import com.usuario.sistemaBD.service.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AutorizacaoService autorizacaoService;

    @PostMapping("/cadastro")
    public ResponseEntity<Usuario> cadastrar(@RequestBody Map<String, String> request) {
        String nome = request.get("nome");
        String email = request.get("email");
        String senha = request.get("senha");
        String confirmarSenha = request.get("confirmarSenha");
        String tipo = request.get("tipo");

        Usuario usuario = usuarioService.cadastrarUsuario(nome, email, senha, confirmarSenha, tipo);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String senha = credentials.get("senha");
        Usuario usuario = usuarioService.fazerLogin(email, senha);

        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/{usuarioId}/avaliacoes")
    public ResponseEntity<Void> adicionarAvaliacao(
            @PathVariable Integer usuarioId,
            @RequestParam Integer instrutorId,
            @RequestParam Double peso,
            @RequestParam Double altura,
            @RequestParam(required = false) String observacoes) {

        //VALIDA SE É INSTRUTOR/ADMIN
        autorizacaoService.validarAcessoInstrutor(instrutorId);

        usuarioService.adicionarAvaliacaoFisica(usuarioId, peso, altura, observacoes);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{usuarioId}/treinos")
    public ResponseEntity<Void> criarPlanoTreino(
            @PathVariable Integer usuarioId,
            @RequestParam Integer instrutorId,
            @RequestParam String nome,
            @RequestParam String objetivo) {

        //VALIDA SE É INSTRUTOR/ADMIN
        autorizacaoService.validarAcessoInstrutor(instrutorId);

        usuarioService.criarPlanoTreino(usuarioId, nome, objetivo);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Usuario> buscarUsuarioPorEmail(@RequestParam String email) {
        return ResponseEntity.ok(usuarioService.buscarUsuarioPorEmail(email));
    }

    @DeleteMapping
    public ResponseEntity<Void> deletarUsuarioPorEmail(@RequestParam String email) {
        usuarioService.deletarUsuarioPorEmail(email);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizarUsuarioPorId(@PathVariable Integer id, @RequestBody Usuario usuario) {
        usuarioService.atualizarUsuarioPorId(id, usuario);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/todos")
    public ResponseEntity<List<Usuario>> listarTodosUsuarios() {
        return ResponseEntity.ok(usuarioService.listarTodosUsuarios());
    }

    @PostMapping("/planos/basico")
    public ResponseEntity<Plano> criarPlanoBasico() {
        return ResponseEntity.ok(usuarioService.criarPlanoBasico());
    }

    @PostMapping("/planos/premium")
    public ResponseEntity<Plano> criarPlanoPremium() {
        return ResponseEntity.ok(usuarioService.criarPlanoPremium());
    }

    @PostMapping("/planos/top")
    public ResponseEntity<Plano> criarPlanoTop() {
        return ResponseEntity.ok(usuarioService.criarPlanoTop());
    }

    @GetMapping("/planos")
    public ResponseEntity<List<Plano>> listarPlanos() {
        return ResponseEntity.ok(usuarioService.listarPlanosAtivos());
    }

    @GetMapping("/planos/{tipo}")
    public ResponseEntity<Plano> buscarPlanoPorTipo(@PathVariable String tipo) {
        Plano.TipoPlano tipoPlano = Plano.TipoPlano.valueOf(tipo.toUpperCase());
        return ResponseEntity.ok(usuarioService.buscarPlanoPorTipo(tipoPlano));
    }

    @GetMapping("/{usuarioId}/avaliacoes")
    public ResponseEntity<List<AvaliacaoFisica.Medida>> buscarHistoricoAvaliacoes(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(usuarioService.buscarHistoricoAvaliacoes(usuarioId));
    }

    @PostMapping("/{usuarioId}/progresso")
    public ResponseEntity<Void> registrarProgresso(@PathVariable Integer usuarioId, @RequestBody Map<String, Object> request) {
        Double peso = Double.parseDouble(request.get("peso").toString());
        Double altura = Double.parseDouble(request.get("altura").toString());
        Integer frequencia = Integer.parseInt(request.get("frequencia").toString());
        Double forca = Double.parseDouble(request.get("forca").toString());
        Double resistencia = Double.parseDouble(request.get("resistencia").toString());
        String observacoes = (String) request.get("observacoes");

        usuarioService.registrarProgresso(usuarioId, peso, altura, frequencia, forca, resistencia, observacoes);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{usuarioId}/graficos")
    public ResponseEntity<Map<String, Object>> getDadosGraficos(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(usuarioService.getDadosGraficos(usuarioId));
    }

    @PostMapping("/{usuarioId}/treinos/exercicios")
    public ResponseEntity<Void> adicionarExercicio(@PathVariable Integer usuarioId, @RequestBody Map<String, String> request) {
        String nome = request.get("nomeExercicio");
        Integer series = Integer.parseInt(request.get("series"));
        String repeticoes = request.get("repeticoes");
        String carga = request.get("carga");
        String observacoes = request.get("observacoes");

        usuarioService.adicionarExercicioPlanoTreino(usuarioId, nome, series, repeticoes, carga, observacoes);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{usuarioId}/treinos")
    public ResponseEntity<PlanoTreino> buscarPlanoTreino(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(usuarioService.buscarPlanoTreino(usuarioId));
    }

    @GetMapping("/treinos/ativos")
    public ResponseEntity<List<PlanoTreino>> listarPlanosTreinoAtivos() {
        return ResponseEntity.ok(usuarioService.listarPlanosTreinoAtivos());
    }

}