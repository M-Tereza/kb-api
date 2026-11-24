package br.edu.ifsudestemg.kb.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "responsavel_tarefa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponsavelTarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_tarefa", nullable = false)
    private Tarefa tarefa;

    @ManyToOne
    @JoinColumn(name = "id_funcionario_projeto", nullable = false)
    private FuncionarioProjeto funcionarioProjeto;
}