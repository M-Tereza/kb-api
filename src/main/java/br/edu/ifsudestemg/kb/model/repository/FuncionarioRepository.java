package br.edu.ifsudestemg.kb.model.repository;

import br.edu.ifsudestemg.kb.model.entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    List<Funcionario> findByCargoId(Long cargoId);
}