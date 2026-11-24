package br.edu.ifsudestemg.kb.service;

import br.edu.ifsudestemg.kb.exception.RegraNegocioException;
import br.edu.ifsudestemg.kb.model.entity.FuncionarioProjeto;
import br.edu.ifsudestemg.kb.model.entity.ResponsavelTarefa;
import br.edu.ifsudestemg.kb.model.entity.Tarefa;
import br.edu.ifsudestemg.kb.model.repository.FuncionarioProjetoRepository;
import br.edu.ifsudestemg.kb.model.repository.ResponsavelTarefaRepository;
import br.edu.ifsudestemg.kb.model.repository.TarefaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResponsavelTarefaService {

    private final ResponsavelTarefaRepository responsavelTarefaRepository;
    private final TarefaRepository tarefaRepository;
    private final FuncionarioProjetoRepository funcionarioProjetoRepository;

    public ResponsavelTarefaService(
            ResponsavelTarefaRepository responsavelTarefaRepository,
            TarefaRepository tarefaRepository,
            FuncionarioProjetoRepository funcionarioProjetoRepository
    ) {
        this.responsavelTarefaRepository = responsavelTarefaRepository;
        this.tarefaRepository = tarefaRepository;
        this.funcionarioProjetoRepository = funcionarioProjetoRepository;
    }

    public List<ResponsavelTarefa> listarPorTarefa(Long tarefaId) {
        return responsavelTarefaRepository.findByTarefaId(tarefaId);
    }

    public List<ResponsavelTarefa> listarPorFuncionarioProjeto(Long funcionarioProjetoId) {
        return responsavelTarefaRepository.findByFuncionarioProjetoId(funcionarioProjetoId);
    }

    public ResponsavelTarefa buscarPorId(Long id) {
        return responsavelTarefaRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Atribuição de responsável não encontrada."));
    }

    @Transactional
    public ResponsavelTarefa salvar(ResponsavelTarefa responsavelTarefa) {
        validar(responsavelTarefa);
        Tarefa tarefa = buscarTarefaVinculada(responsavelTarefa.getTarefa());
        FuncionarioProjeto funcionarioProjeto = buscarFuncionarioProjetoVinculado(responsavelTarefa.getFuncionarioProjeto());
        validarPertencemAoMesmoProjeto(tarefa, funcionarioProjeto);

        responsavelTarefa.setTarefa(tarefa);
        responsavelTarefa.setFuncionarioProjeto(funcionarioProjeto);
        return responsavelTarefaRepository.save(responsavelTarefa);
    }

    @Transactional
    public ResponsavelTarefa atualizar(Long id, ResponsavelTarefa dados) {
        ResponsavelTarefa existente = buscarPorId(id);
        validar(dados);

        Tarefa tarefa;
        if (dados.getTarefa() != null) {
            tarefa = buscarTarefaVinculada(dados.getTarefa());
        } else {
            tarefa = existente.getTarefa();
        }

        FuncionarioProjeto funcionarioProjeto;
        if (dados.getFuncionarioProjeto() != null) {
            funcionarioProjeto = buscarFuncionarioProjetoVinculado(dados.getFuncionarioProjeto());
        } else {
            funcionarioProjeto = existente.getFuncionarioProjeto();
        }

        validarPertencemAoMesmoProjeto(tarefa, funcionarioProjeto);

        existente.setTarefa(tarefa);
        existente.setFuncionarioProjeto(funcionarioProjeto);
        return responsavelTarefaRepository.save(existente);
    }

    @Transactional
    public void excluir(Long id) {
        ResponsavelTarefa existente = buscarPorId(id);
        responsavelTarefaRepository.delete(existente);
    }

    public void validar(ResponsavelTarefa responsavelTarefa) {
        if (responsavelTarefa == null) {
            throw new RegraNegocioException("Dados da atribuição de responsável são obrigatórios.");
        }
    }

    public Tarefa buscarTarefaVinculada(Tarefa tarefa) {
        if (tarefa == null || tarefa.getId() == null) {
            throw new RegraNegocioException("A tarefa é obrigatória.");
        }
        return tarefaRepository.findById(tarefa.getId())
                .orElseThrow(() -> new RegraNegocioException("Tarefa informada não encontrada."));
    }

    public FuncionarioProjeto buscarFuncionarioProjetoVinculado(FuncionarioProjeto funcionarioProjeto) {
        if (funcionarioProjeto == null || funcionarioProjeto.getId() == null) {
            throw new RegraNegocioException("O funcionário vinculado ao projeto é obrigatório.");
        }
        return funcionarioProjetoRepository.findById(funcionarioProjeto.getId())
                .orElseThrow(() -> new RegraNegocioException("Vínculo de funcionário no projeto não encontrado."));
    }

    public void validarPertencemAoMesmoProjeto(Tarefa tarefa, FuncionarioProjeto funcionarioProjeto) {
        Long projetoTarefaId;
        if (tarefa.getFase() != null && tarefa.getFase().getProjeto() != null) {
            projetoTarefaId = tarefa.getFase().getProjeto().getId();
        } else {
            projetoTarefaId = null;
        }

        Long projetoVinculoId;
        if (funcionarioProjeto.getProjeto() != null) {
            projetoVinculoId = funcionarioProjeto.getProjeto().getId();
        } else {
            projetoVinculoId = null;
        }

        if (projetoTarefaId == null || projetoVinculoId == null || !projetoTarefaId.equals(projetoVinculoId)) {
            throw new RegraNegocioException("O funcionário informado não pertence ao mesmo projeto da tarefa.");
        }
    }
}