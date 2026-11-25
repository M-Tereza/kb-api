package br.edu.ifsudestemg.kb.service;

import br.edu.ifsudestemg.kb.exception.RegraNegocioException;
import br.edu.ifsudestemg.kb.model.entity.Prioridade;
import br.edu.ifsudestemg.kb.model.repository.PrioridadeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PrioridadeService {

    private static final int TAMANHO_MINIMO_NOME = 2;
    private static final int TAMANHO_MAXIMO_NOME = 60;

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
        if (prioridade.getNome() == null) {
            throw new RegraNegocioException("O nome da prioridade é obrigatório.");
        }
        String nomeTrimmed = prioridade.getNome().trim();
        if (nomeTrimmed.isEmpty()) {
            throw new RegraNegocioException("O nome da prioridade não pode estar em branco.");
        }
        if (nomeTrimmed.length() < TAMANHO_MINIMO_NOME) {
            throw new RegraNegocioException("O nome da prioridade deve ter pelo menos " + TAMANHO_MINIMO_NOME + " caracteres.");
        }
        if (nomeTrimmed.length() > TAMANHO_MAXIMO_NOME) {
            throw new RegraNegocioException("O nome da prioridade deve ter no máximo " + TAMANHO_MAXIMO_NOME + " caracteres.");
        }
    }
}