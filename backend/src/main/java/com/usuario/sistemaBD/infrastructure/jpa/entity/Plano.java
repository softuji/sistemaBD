package com.usuario.sistemaBD.infrastructure.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "plano")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plano {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPlano tipo; // BASICO, PREMIUM, TOP

    @Column(name = "valor_mensal", nullable = false)
    private double valorMensal;

    @ElementCollection
    @CollectionTable(name = "plano_modalidade", joinColumns = @JoinColumn(name = "plano_id"))
    @Column(name = "modalidade")
    @Enumerated(EnumType.STRING)
    private List<Modalidade> modalidadesPermitidas;

    @Builder.Default
    private boolean ativo = true;

    public enum TipoPlano {
        BASICO, PREMIUM, TOP
    }

    public enum Modalidade {
        MUSCULACAO, YOGA, PILATES, NATACAO, CROSSFIT, DANCA, LUTAS
    }
}