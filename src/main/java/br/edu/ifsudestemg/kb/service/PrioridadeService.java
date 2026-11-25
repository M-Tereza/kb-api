package br.edu.ifsudestemg.kb.service;

import br.edu.ifsudestemg.kb.exception.RegraNegocioException;
import br.edu.ifsudestemg.kb.model.entity.Prioridade;
import br.edu.ifsudestemg.kb.model.repository.PrioridadeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class PrioridadeService {

    private static final int TAMANHO_MINIMO_NOME = 2;
    private static final int TAMANHO_MAXIMO_NOME = 60;
    private static final String COR_PADRAO = "#3B82F6";
    private static final String REGEX_COR_HEX = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";

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
        prioridade.setCor(normalizarCor(prioridade.getCor()));
        return prioridadeRepository.save(prioridade);
    }

    @Transactional
    public Prioridade atualizar(Long id, Prioridade dados) {
        Prioridade existente = buscarPorId(id);
        validar(dados);
        existente.setNome(dados.getNome().trim());
        existente.setCor(normalizarCor(dados.getCor()));
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
        if (prioridade.getCor() != null && !prioridade.getCor().trim().isEmpty()) {
            String corTrimmed = prioridade.getCor().trim();
            if (!corTrimmed.matches(REGEX_COR_HEX)) {
                throw new RegraNegocioException("A cor da prioridade deve estar no formato hexadecimal (ex: #3B82F6).");
            }
        }
    }

    public String normalizarCor(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return COR_PADRAO;
        }
        return valor.trim().toUpperCase(Locale.ROOT);
    }
}