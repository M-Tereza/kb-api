package br.edu.ifsudestemg.kb.service;

import br.edu.ifsudestemg.kb.exception.RegraNegocioException;
import br.edu.ifsudestemg.kb.model.entity.Anotacao;
import br.edu.ifsudestemg.kb.model.entity.Tarefa;
import br.edu.ifsudestemg.kb.model.repository.AnotacaoRepository;
import br.edu.ifsudestemg.kb.model.repository.TarefaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class AnotacaoService {

    private final AnotacaoRepository anotacaoRepository;
    private final TarefaRepository tarefaRepository;

    public AnotacaoService(AnotacaoRepository anotacaoRepository, TarefaRepository tarefaRepository) {
        this.anotacaoRepository = anotacaoRepository;
        this.tarefaRepository = tarefaRepository;
    }

    public List<Anotacao> listarPorTarefa(Long tarefaId) {
        return anotacaoRepository.findByTarefaId(tarefaId);
    }

    public Anotacao buscarPorId(Long id) {
        return anotacaoRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Anotação não encontrada."));
    }

    @Transactional
    public Anotacao salvar(Anotacao anotacao) {
        validar(anotacao);
        Tarefa tarefa = buscarTarefaVinculada(anotacao.getTarefa());

        anotacao.setTexto(anotacao.getTexto().trim());
        if (anotacao.getDataCriacao() == null) {
            anotacao.setDataCriacao(Instant.now());
        }
        anotacao.setTarefa(tarefa);
        return anotacaoRepository.save(anotacao);
    }

    @Transactional
    public Anotacao atualizar(Long id, Anotacao dados) {
        Anotacao existente = buscarPorId(id);
        validar(dados);

        Tarefa tarefa;
        if (dados.getTarefa() != null) {
            tarefa = buscarTarefaVinculada(dados.getTarefa());
        } else {
            tarefa = existente.getTarefa();
        }

        existente.setTexto(dados.getTexto().trim());
        existente.setTarefa(tarefa);
        return anotacaoRepository.save(existente);
    }

    @Transactional
    public void excluir(Long id) {
        Anotacao existente = buscarPorId(id);
        anotacaoRepository.delete(existente);
    }

    public void validar(Anotacao anotacao) {
        if (anotacao == null) {
            throw new RegraNegocioException("Dados da anotação são obrigatórios.");
        }
        if (anotacao.getTexto() == null || anotacao.getTexto().trim().isEmpty()) {
            throw new RegraNegocioException("O texto da anotação é obrigatório.");
        }
    }

    public Tarefa buscarTarefaVinculada(Tarefa tarefa) {
        if (tarefa == null || tarefa.getId() == null) {
            throw new RegraNegocioException("A tarefa da anotação é obrigatória.");
        }
        return tarefaRepository.findById(tarefa.getId())
                .orElseThrow(() -> new RegraNegocioException("Tarefa informada não encontrada."));
    }
}