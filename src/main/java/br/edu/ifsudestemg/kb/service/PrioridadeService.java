package br.edu.ifsudestemg.kb.service;

import br.edu.ifsudestemg.kb.exception.RegraNegocioException;
import br.edu.ifsudestemg.kb.model.entity.Prioridade;
import br.edu.ifsudestemg.kb.model.repository.PrioridadeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PrioridadeService {

    private final PrioridadeRepository prioridadeRepository;

    public PrioridadeService(PrioridadeRepository prioridadeRepository) {
        this.prioridadeRepository = prioridadeRepository;
    }

    public List<Prioridade> listarTodas() {
        return prioridadeRepository.findAll();
    }

    public Prioridade buscarPorId(Long id) {
        return prioridadeRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Prioridade não encontrada."));
    }

    @Transactional
    public Prioridade salvar(Prioridade prioridade) {
        validar(prioridade);
        prioridade.setNome(prioridade.getNome().trim());
        return prioridadeRepository.save(prioridade);
    }

    @Transactional
    public Prioridade atualizar(Long id, Prioridade dados) {
        Prioridade existente = buscarPorId(id);
        validar(dados);
        existente.setNome(dados.getNome().trim());
        return prioridadeRepository.save(existente);
    }

    @Transactional
    public void excluir(Long id) {
        Prioridade existente = buscarPorId(id);
        prioridadeRepository.delete(existente);
    }

    public void validar(Prioridade prioridade) {
        if (prioridade == null) {
            throw new RegraNegocioException("Dados da prioridade são obrigatórios.");
        }
        if (prioridade.getNome() == null || prioridade.getNome().trim().isEmpty()) {
            throw new RegraNegocioException("O nome da prioridade é obrigatório.");
        }
    }
}