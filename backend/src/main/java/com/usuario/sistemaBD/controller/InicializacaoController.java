package com.usuario.sistemaBD.controller;

import com.usuario.sistemaBD.service.InicializacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/inicializacao")
public class InicializacaoController {

    private final InicializacaoService inicializacaoService;

    //CRIAR DOCUMENTOS MONGODB PARA USUÁRIO
    @PostMapping("/{usuarioId}/documentos")
    public ResponseEntity<Void> criarDocumentosUsuario(@PathVariable Integer usuarioId) {
        inicializacaoService.criarDocumentosMongoDB(usuarioId);
        return ResponseEntity.ok().build();
    }

    //VERIFICAR SE USUÁRIO TEM DOCUMENTOS
    @GetMapping("/{usuarioId}/documentos/existe")
    public ResponseEntity<Boolean> verificarDocumentosExistem(@PathVariable Integer usuarioId) {
        boolean existe = inicializacaoService.verificarDocumentosExistem(usuarioId);
        return ResponseEntity.ok(existe);
    }

    //REINICIALIZAR DOCUMENTOS (APENAS ADMIN)
    @PostMapping("/{usuarioId}/documentos/reiniciar")
    public ResponseEntity<Void> reinicializarDocumentos(@PathVariable Integer usuarioId) {
        inicializacaoService.reinicializarDocumentos(usuarioId);
        return ResponseEntity.ok().build();
    }
}