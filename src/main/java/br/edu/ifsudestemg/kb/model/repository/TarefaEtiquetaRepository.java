package br.edu.ifsudestemg.kb.model.repository;

import br.edu.ifsudestemg.kb.model.entity.TarefaEtiqueta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TarefaEtiquetaRepository extends JpaRepository<TarefaEtiqueta, Long> {

    List<TarefaEtiqueta> findByTarefaId(Long tarefaId);

    List<TarefaEtiqueta> findByEtiquetaId(Long etiquetaId);

    void deleteByTarefaId(Long tarefaId);

    void deleteByEtiquetaId(Long etiquetaId);
}