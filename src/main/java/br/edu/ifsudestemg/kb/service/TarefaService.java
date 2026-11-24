package br.edu.ifsudestemg.kb.service;

import br.edu.ifsudestemg.kb.exception.RegraNegocioException;
import br.edu.ifsudestemg.kb.model.entity.Fase;
import br.edu.ifsudestemg.kb.model.entity.Prioridade;
import br.edu.ifsudestemg.kb.model.entity.Projeto;
import br.edu.ifsudestemg.kb.model.entity.Tarefa;
import br.edu.ifsudestemg.kb.model.repository.AnotacaoRepository;
import br.edu.ifsudestemg.kb.model.repository.FaseRepository;
import br.edu.ifsudestemg.kb.model.repository.PrioridadeRepository;
import br.edu.ifsudestemg.kb.model.repository.ProjetoRepository;
import br.edu.ifsudestemg.kb.model.repository.ResponsavelTarefaRepository;
import br.edu.ifsudestemg.kb.model.repository.TarefaEtiquetaRepository;
import br.edu.ifsudestemg.kb.model.repository.TarefaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class TarefaService {

    private static final int TAMANHO_MAXIMO_TITULO = 120;
    private static final int TAMANHO_MAXIMO_DESCRICAO = 500;

    private final TarefaRepository tarefaRepository;
    private final ProjetoRepository projetoRepository;
    private final FaseRepository faseRepository;
    private final PrioridadeRepository prioridadeRepository;
    private final AnotacaoRepository anotacaoRepository;
    private final ResponsavelTarefaRepository responsavelTarefaRepository;
    private final TarefaEtiquetaRepository tarefaEtiquetaRepository;

    public TarefaService(
            TarefaRepository tarefaRepository,
            ProjetoRepository projetoRepository,
            FaseRepository faseRepository,
            PrioridadeRepository prioridadeRepository,
            AnotacaoRepository anotacaoRepository,
            ResponsavelTarefaRepository responsavelTarefaRepository,
            TarefaEtiquetaRepository tarefaEtiquetaRepository
    ) {
        this.tarefaRepository = tarefaRepository;
        this.projetoRepository = projetoRepository;
        this.faseRepository = faseRepository;
        this.prioridadeRepository = prioridadeRepository;
        this.anotacaoRepository = anotacaoRepository;
        this.responsavelTarefaRepository = responsavelTarefaRepository;
        this.tarefaEtiquetaRepository = tarefaEtiquetaRepository;
    }

    public List<Tarefa> listarTodos() {
        return tarefaRepository.findAll();
    }

    public List<Tarefa> listarPorProjeto(Long projetoId) {
        return tarefaRepository.findByProjetoId(projetoId);
    }

    public List<Tarefa> listarPorFase(Long faseId) {
        return tarefaRepository.findByFaseId(faseId);
    }

    public Tarefa buscarPorId(Long id) {
        return tarefaRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Tarefa não encontrada."));
    }

    @Transactional
    public Tarefa salvar(Tarefa tarefa) {
        validar(tarefa);
        Projeto projeto = buscarProjetoVinculado(tarefa.getProjeto());
        Fase fase = buscarFaseVinculada(tarefa.getFase());
        Prioridade prioridade = buscarPrioridadeVinculada(tarefa.getPrioridade());
        validarPertenciaFaseAoProjeto(fase, projeto);

        tarefa.setTitulo(tarefa.getTitulo().trim());
        tarefa.setDescricao(trimOuNulo(tarefa.getDescricao()));
        if (tarefa.getDataCriacao() == null) {
            tarefa.setDataCriacao(Instant.now());
        }
        tarefa.setProjeto(projeto);
        tarefa.setFase(fase);
        tarefa.setPrioridade(prioridade);
        return tarefaRepository.save(tarefa);
    }

    @Transactional
    public Tarefa atualizar(Long id, Tarefa dados) {
        Tarefa existente = buscarPorId(id);
        validar(dados);
        Projeto projeto = buscarProjetoVinculado(dados.getProjeto());
        Fase fase = buscarFaseVinculada(dados.getFase());
        Prioridade prioridade = buscarPrioridadeVinculada(dados.getPrioridade());
        validarPertenciaFaseAoProjeto(fase, projeto);

        existente.setTitulo(dados.getTitulo().trim());
        existente.setDescricao(trimOuNulo(dados.getDescricao()));
        existente.setDataLimite(dados.getDataLimite());
        existente.setProjeto(projeto);
        existente.setFase(fase);
        existente.setPrioridade(prioridade);
        return tarefaRepository.save(existente);
    }

    @Transactional
    public void excluir(Long id) {
        Tarefa existente = buscarPorId(id);
        anotacaoRepository.deleteByTarefaId(id);
        responsavelTarefaRepository.deleteByTarefaId(id);
        tarefaEtiquetaRepository.deleteByTarefaId(id);
        tarefaRepository.delete(existente);
    }

    public void validar(Tarefa tarefa) {
        if (tarefa == null) {
            throw new RegraNegocioException("Dados da tarefa são obrigatórios.");
        }
        if (tarefa.getTitulo() == null) {
            throw new RegraNegocioException("O título da tarefa é obrigatório.");
        }
        String tituloTrimmed = tarefa.getTitulo().trim();
        if (tituloTrimmed.isEmpty()) {
            throw new RegraNegocioException("O título da tarefa não pode estar em branco.");
        }
        if (tituloTrimmed.length() > TAMANHO_MAXIMO_TITULO) {
            throw new RegraNegocioException("O título da tarefa deve ter no máximo " + TAMANHO_MAXIMO_TITULO + " caracteres.");
        }
        if (tarefa.getDescricao() != null) {
            if (tarefa.getDescricao().trim().length() > TAMANHO_MAXIMO_DESCRICAO) {
                throw new RegraNegocioException("A descrição da tarefa deve ter no máximo " + TAMANHO_MAXIMO_DESCRICAO + " caracteres.");
            }
        }
        if (tarefa.getProjeto() == null) {
            throw new RegraNegocioException("O projeto da tarefa é obrigatório.");
        }
        if (tarefa.getProjeto().getId() == null) {
            throw new RegraNegocioException("O identificador do projeto da tarefa é obrigatório.");
        }
        if (tarefa.getFase() == null) {
            throw new RegraNegocioException("A fase da tarefa é obrigatória.");
        }
        if (tarefa.getFase().getId() == null) {
            throw new RegraNegocioException("O identificador da fase da tarefa é obrigatório.");
        }
        if (tarefa.getPrioridade() == null) {
            throw new RegraNegocioException("A prioridade da tarefa é obrigatória.");
        }
        if (tarefa.getPrioridade().getId() == null) {
            throw new RegraNegocioException("O identificador da prioridade da tarefa é obrigatório.");
        }
        if (tarefa.getDataCriacao() != null && tarefa.getDataLimite() != null
                && tarefa.getDataLimite().isBefore(tarefa.getDataCriacao())) {
            throw new RegraNegocioException("A data limite não pode ser anterior à data de criação.");
        }
    }

    public Projeto buscarProjetoVinculado(Projeto projeto) {
        if (projeto == null || projeto.getId() == null) {
            throw new RegraNegocioException("O projeto da tarefa é obrigatório.");
        }
        return projetoRepository.findById(projeto.getId())
                .orElseThrow(() -> new RegraNegocioException("Projeto informado não encontrado."));
    }

    public Fase buscarFaseVinculada(Fase fase) {
        if (fase == null || fase.getId() == null) {
            throw new RegraNegocioException("A fase da tarefa é obrigatória.");
        }
        return faseRepository.findById(fase.getId())
                .orElseThrow(() -> new RegraNegocioException("Fase informada não encontrada."));
    }

    public Prioridade buscarPrioridadeVinculada(Prioridade prioridade) {
        if (prioridade == null || prioridade.getId() == null) {
            throw new RegraNegocioException("A prioridade da tarefa é obrigatória.");
        }
        return prioridadeRepository.findById(prioridade.getId())
                .orElseThrow(() -> new RegraNegocioException("Prioridade informada não encontrada."));
    }

    public String trimOuNulo(String valor) {
        if (valor != null) {
            return valor.trim();
        } else {
            return null;
        }
    }

    public void validarPertenciaFaseAoProjeto(Fase fase, Projeto projeto) {
        if (fase.getProjeto() == null || !fase.getProjeto().getId().equals(projeto.getId())) {
            throw new RegraNegocioException("A fase informada não pertence ao projeto da tarefa.");
        }
    }
}