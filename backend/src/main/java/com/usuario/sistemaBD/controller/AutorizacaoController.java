package com.usuario.sistemaBD.controller;

import com.usuario.sistemaBD.service.AutorizacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/autorizacao")
public class AutorizacaoController {

    private final AutorizacaoService autorizacaoService;

    public AutorizacaoController(AutorizacaoService autorizacaoService) {
        this.autorizacaoService = autorizacaoService;
    }

    @GetMapping("/{usuarioId}/instrutor")
    public ResponseEntity<Void> validarAcessoInstrutor(@PathVariable Integer usuarioId) {
        autorizacaoService.validarAcessoInstrutor(usuarioId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{usuarioId}/admin")
    public ResponseEntity<Void> validarAcessoAdmin(@PathVariable Integer usuarioId) {
        autorizacaoService.validarAcessoAdmin(usuarioId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{usuarioId}/tipo")
    public ResponseEntity<String> obterTipoUsuario(@PathVariable Integer usuarioId) {
        String tipo = autorizacaoService.obterTipoUsuario(usuarioId);
        return ResponseEntity.ok(tipo);

    }
}