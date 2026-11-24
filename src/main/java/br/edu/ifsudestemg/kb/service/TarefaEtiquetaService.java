package br.edu.ifsudestemg.kb.service;

import br.edu.ifsudestemg.kb.exception.RegraNegocioException;
import br.edu.ifsudestemg.kb.model.entity.Etiqueta;
import br.edu.ifsudestemg.kb.model.entity.Tarefa;
import br.edu.ifsudestemg.kb.model.entity.TarefaEtiqueta;
import br.edu.ifsudestemg.kb.model.repository.EtiquetaRepository;
import br.edu.ifsudestemg.kb.model.repository.TarefaEtiquetaRepository;
import br.edu.ifsudestemg.kb.model.repository.TarefaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TarefaEtiquetaService {

    private final TarefaEtiquetaRepository tarefaEtiquetaRepository;
    private final TarefaRepository tarefaRepository;
    private final EtiquetaRepository etiquetaRepository;

    public TarefaEtiquetaService(
            TarefaEtiquetaRepository tarefaEtiquetaRepository,
            TarefaRepository tarefaRepository,
            EtiquetaRepository etiquetaRepository
    ) {
        this.tarefaEtiquetaRepository = tarefaEtiquetaRepository;
        this.tarefaRepository = tarefaRepository;
        this.etiquetaRepository = etiquetaRepository;
    }

    public List<TarefaEtiqueta> listarPorTarefa(Long tarefaId) {
        return tarefaEtiquetaRepository.findByTarefaId(tarefaId);
    }

    public List<TarefaEtiqueta> listarPorEtiqueta(Long etiquetaId) {
        return tarefaEtiquetaRepository.findByEtiquetaId(etiquetaId);
    }

    public TarefaEtiqueta buscarPorId(Long id) {
        return tarefaEtiquetaRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Vínculo de etiqueta na tarefa não encontrado."));
    }

    @Transactional
    public TarefaEtiqueta salvar(TarefaEtiqueta vinculo) {
        validar(vinculo);
        Tarefa tarefa = buscarTarefaVinculada(vinculo.getTarefa());
        Etiqueta etiqueta = buscarEtiquetaVinculada(vinculo.getEtiqueta());
        validarPertencemAoMesmoProjeto(tarefa, etiqueta);

        vinculo.setTarefa(tarefa);
        vinculo.setEtiqueta(etiqueta);
        return tarefaEtiquetaRepository.save(vinculo);
    }

    @Transactional
    public TarefaEtiqueta atualizar(Long id, TarefaEtiqueta dados) {
        TarefaEtiqueta existente = buscarPorId(id);
        validar(dados);

        Tarefa tarefa;
        if (dados.getTarefa() != null) {
            tarefa = buscarTarefaVinculada(dados.getTarefa());
        } else {
            tarefa = existente.getTarefa();
        }

        Etiqueta etiqueta;
        if (dados.getEtiqueta() != null) {
            etiqueta = buscarEtiquetaVinculada(dados.getEtiqueta());
        } else {
            etiqueta = existente.getEtiqueta();
        }

        validarPertencemAoMesmoProjeto(tarefa, etiqueta);

        existente.setTarefa(tarefa);
        existente.setEtiqueta(etiqueta);
        return tarefaEtiquetaRepository.save(existente);
    }

    @Transactional
    public void excluir(Long id) {
        TarefaEtiqueta existente = buscarPorId(id);
        tarefaEtiquetaRepository.delete(existente);
    }

    public void validar(TarefaEtiqueta vinculo) {
        if (vinculo == null) {
            throw new RegraNegocioException("Dados do vínculo da etiqueta são obrigatórios.");
        }
    }

    public Tarefa buscarTarefaVinculada(Tarefa tarefa) {
        if (tarefa == null || tarefa.getId() == null) {
            throw new RegraNegocioException("A tarefa do vínculo é obrigatória.");
        }
        return tarefaRepository.findById(tarefa.getId())
                .orElseThrow(() -> new RegraNegocioException("Tarefa informada não encontrada."));
    }

    public Etiqueta buscarEtiquetaVinculada(Etiqueta etiqueta) {
        if (etiqueta == null || etiqueta.getId() == null) {
            throw new RegraNegocioException("A etiqueta do vínculo é obrigatória.");
        }
        return etiquetaRepository.findById(etiqueta.getId())
                .orElseThrow(() -> new RegraNegocioException("Etiqueta informada não encontrada."));
    }

    public void validarPertencemAoMesmoProjeto(Tarefa tarefa, Etiqueta etiqueta) {
        Long projetoTarefaId;
        if (tarefa.getFase() != null && tarefa.getFase().getProjeto() != null) {
            projetoTarefaId = tarefa.getFase().getProjeto().getId();
        } else {
            projetoTarefaId = null;
        }

        Long projetoEtiquetaId;
        if (etiqueta.getProjeto() != null) {
            projetoEtiquetaId = etiqueta.getProjeto().getId();
        } else {
            projetoEtiquetaId = null;
        }

        if (projetoTarefaId == null || projetoEtiquetaId == null || !projetoTarefaId.equals(projetoEtiquetaId)) {
            throw new RegraNegocioException("A etiqueta informada não pertence ao mesmo projeto da tarefa.");
        }
    }
}