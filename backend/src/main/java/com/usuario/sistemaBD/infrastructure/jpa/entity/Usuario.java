package com.usuario.sistemaBD.infrastructure.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false)
    private String email;

    private String telefone;

    @Column(nullable = false)
    private String senha;

    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TipoUsuario tipo = TipoUsuario.ALUNO;

    @Column(name = "data_cadastro", nullable = false)
    @Builder.Default
    private LocalDate dataCadastro = LocalDate.now();

    @Builder.Default
    private boolean ativo = true;

    @ManyToOne
    @JoinColumn(name = "plano_id")
    private Plano plano;

    private String mongoAvaliacoesId;
    private String mongoTreinosId;

    public enum TipoUsuario {
        ALUNO, INSTRUTOR, ADMIN
    }
}