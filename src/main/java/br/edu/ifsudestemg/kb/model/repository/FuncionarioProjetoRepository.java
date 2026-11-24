package br.edu.ifsudestemg.kb.model.repository;

import br.edu.ifsudestemg.kb.model.entity.FuncionarioProjeto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FuncionarioProjetoRepository extends JpaRepository<FuncionarioProjeto, Long> {

    List<FuncionarioProjeto> findByProjetoId(Long projetoId);

    List<FuncionarioProjeto> findByFuncionarioId(Long funcionarioId);

    void deleteByProjetoId(Long projetoId);
}