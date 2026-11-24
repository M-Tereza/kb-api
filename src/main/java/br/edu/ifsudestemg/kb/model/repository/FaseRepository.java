package br.edu.ifsudestemg.kb.model.repository;

import br.edu.ifsudestemg.kb.model.entity.Fase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaseRepository extends JpaRepository<Fase, Long> {

    List<Fase> findByProjetoIdOrderByOrdemAsc(Long projetoId);

    boolean existsByProjetoIdAndOrdem(Long projetoId, Integer ordem);

    boolean existsByProjetoIdAndOrdemAndIdNot(Long projetoId, Integer ordem, Long id);

    void deleteByProjetoId(Long projetoId);
}