package br.edu.ifsudestemg.kb.model.repository;

import br.edu.ifsudestemg.kb.model.entity.ResponsavelTarefa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResponsavelTarefaRepository extends JpaRepository<ResponsavelTarefa, Long> {

    List<ResponsavelTarefa> findByTarefaId(Long tarefaId);

    List<ResponsavelTarefa> findByFuncionarioProjetoId(Long funcionarioProjetoId);

    void deleteByTarefaId(Long tarefaId);

    void deleteByFuncionarioProjetoId(Long funcionarioProjetoId);
}