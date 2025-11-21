package com.usuario.sistemaBD.infrastructure.mongodb.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "plano_treino")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanoTreino {

    @Id
    private String id;

    private Integer usuarioId;

    private String nome;
    private String objetivo;

    @Builder.Default
    private LocalDate dataCriacao = LocalDate.now();

    @Builder.Default
    private boolean ativo = true;

    @Builder.Default
    private List<Exercicio> exercicios = new ArrayList<>();

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Exercicio {
        private String nome;
        private Integer series;
        private String repeticoes;
        private String carga;
        private String observacoes;
    }
}