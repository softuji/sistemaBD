package com.usuario.sistemaBD.infrastructure.mongodb.document;

import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Document(collection = "avaliacao_fisica")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvaliacaoFisica {

    @Id
    private String id;

    private Integer usuarioId; // Referência ao PostgreSQL

    @Builder.Default
    private List<Medida> medidas = new ArrayList<>();

    @Builder.Default
    private Estatisticas estatisticas = new Estatisticas();

    @Setter
    @Getter
    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Medida {
        private LocalDate data;
        private Double peso;
        private Double altura;
        private Double imc;
        private Integer frequenciaSemanal;
        private Double forcaMedia; // Para gráfico de evolução de força
        private Double resistencia; // Para gráfico de resistência
        private String observacoes;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Estatisticas {
        private Double pesoInicial;
        private Double pesoAtual;
        private Double variacaoPeso;
        private Double imcInicial;
        private Double imcAtual;
        private Integer totalSessoes;
        private Double mediaFrequenciaSemanal;
        private Map<String, Double> evolucaoMedidas; // Variação das medidas
    }
}
