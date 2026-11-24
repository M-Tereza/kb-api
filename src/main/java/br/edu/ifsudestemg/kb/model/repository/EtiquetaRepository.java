package br.edu.ifsudestemg.kb.model.repository;

import br.edu.ifsudestemg.kb.model.entity.Etiqueta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EtiquetaRepository extends JpaRepository<Etiqueta, Long> {

    List<Etiqueta> findByProjetoId(Long projetoId);

    void deleteByProjetoId(Long projetoId);
}