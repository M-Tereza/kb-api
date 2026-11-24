package br.edu.ifsudestemg.kb.model.repository;

import br.edu.ifsudestemg.kb.model.entity.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    List<Tarefa> findByFaseProjetoId(Long projetoId);

    List<Tarefa> findByFaseId(Long faseId);

    void deleteByFaseProjetoId(Long projetoId);

    void deleteByFaseId(Long faseId);
}