package br.edu.ifsudestemg.kb.model.repository;

import br.edu.ifsudestemg.kb.model.entity.Anotacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnotacaoRepository extends JpaRepository<Anotacao, Long> {

    List<Anotacao> findByTarefaId(Long tarefaId);

    void deleteByTarefaId(Long tarefaId);
}