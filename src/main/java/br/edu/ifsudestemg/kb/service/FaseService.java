package br.edu.ifsudestemg.kb.service;

import br.edu.ifsudestemg.kb.exception.RegraNegocioException;
import br.edu.ifsudestemg.kb.model.entity.Fase;
import br.edu.ifsudestemg.kb.model.entity.Projeto;
import br.edu.ifsudestemg.kb.model.entity.Tarefa;
import br.edu.ifsudestemg.kb.model.repository.FaseRepository;
import br.edu.ifsudestemg.kb.model.repository.ProjetoRepository;
import br.edu.ifsudestemg.kb.model.repository.TarefaRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FaseService {

    private static final int TAMANHO_MINIMO_NOME = 2;
    private static final int TAMANHO_MAXIMO_NOME = 60;

    private final FaseRepository faseRepository;
    private final ProjetoRepository projetoRepository;
    private final TarefaRepository tarefaRepository;
    private final TarefaService tarefaService;

    public FaseService(
            FaseRepository faseRepository,
            ProjetoRepository projetoRepository,
            TarefaRepository tarefaRepository,
            @Lazy TarefaService tarefaService
    ) {
        this.faseRepository = faseRepository;
        this.projetoRepository = projetoRepository;
        this.tarefaRepository = tarefaRepository;
        this.tarefaService = tarefaService;
    }

    public List<Fase> listarPorProjeto(Long projetoId) {
        return faseRepository.findByProjetoIdOrderByOrdemAsc(projetoId);
    }

    public Fase buscarPorId(Long id) {
        return faseRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Fase não encontrada."));
    }

    @Transactional
    public Fase salvar(Fase fase) {
        validar(fase);
        Projeto projeto = buscarProjetoVinculado(fase.getProjeto());
        List<Fase> fasesExistentes = faseRepository.findByProjetoIdOrderByOrdemAsc(projeto.getId());
        int limiteMaximo = fasesExistentes.size() + 1;
        validarLimitesOrdem(fase.getOrdem(), limiteMaximo);
        validarOrdemUnica(null, projeto.getId(), fase.getOrdem());

        fase.setNome(fase.getNome().trim());
        fase.setProjeto(projeto);
        return faseRepository.save(fase);
    }

    @Transactional
    public Fase atualizar(Long id, Fase dados) {
        Fase existente = buscarPorId(id);
        validar(dados);

        Projeto projeto;
        if (dados.getProjeto() != null && dados.getProjeto().getId() != null) {
            projeto = buscarProjetoVinculado(dados.getProjeto());
        } else {
            projeto = existente.getProjeto();
        }

        List<Fase> fasesDoProjeto = faseRepository.findByProjetoIdOrderByOrdemAsc(projeto.getId());
        int limiteMaximo = fasesDoProjeto.size();
        validarLimitesOrdem(dados.getOrdem(), limiteMaximo);
        validarOrdemUnica(id, projeto.getId(), dados.getOrdem());

        existente.setNome(dados.getNome().trim());
        existente.setOrdem(dados.getOrdem());
        existente.setEhFaseFinal(dados.getEhFaseFinal());
        existente.setProjeto(projeto);
        return faseRepository.save(existente);
    }

    @Transactional
    public Fase moverEsquerda(Long faseId) {
        return trocarOrdemComVizinha(faseId, -1);
    }

    @Transactional
    public Fase moverDireita(Long faseId) {
        return trocarOrdemComVizinha(faseId, 1);
    }

    @Transactional
    public void excluir(Long id, boolean confirmarExclusaoTarefas) {
        Fase existente = buscarPorId(id);
        Long projetoId = existente.getProjeto().getId();
        List<Tarefa> tarefasNaFase = tarefaRepository.findByFaseId(id);

        if (!tarefasNaFase.isEmpty() && !confirmarExclusaoTarefas) {
            throw new RegraNegocioException(
                    "A fase possui tarefas alocadas. Confirme o aviso prévio para excluir a fase e as tarefas vinculadas."
            );
        }

        for (Tarefa tarefa : tarefasNaFase) {
            tarefaService.excluir(tarefa.getId());
        }

        faseRepository.delete(existente);
        faseRepository.flush();

        recalcularOrdemFases(projetoId);
    }

    @Transactional
    public void recalcularOrdemFases(Long projetoId) {
        List<Fase> fasesRestantes = faseRepository.findByProjetoIdOrderByOrdemAsc(projetoId);
        if (fasesRestantes.isEmpty()) {
            return;
        }

        for (int i = 0; i < fasesRestantes.size(); i++) {
            fasesRestantes.get(i).setOrdem(-(i + 1));
        }
        faseRepository.saveAllAndFlush(fasesRestantes);

        for (int i = 0; i < fasesRestantes.size(); i++) {
            fasesRestantes.get(i).setOrdem(i + 1);
        }
        faseRepository.saveAllAndFlush(fasesRestantes);
    }

    public void validar(Fase fase) {
        if (fase == null) {
            throw new RegraNegocioException("Dados da fase são obrigatórios.");
        }
        if (fase.getNome() == null) {
            throw new RegraNegocioException("O nome da fase é obrigatório.");
        }
        String nomeTrimmed = fase.getNome().trim();
        if (nomeTrimmed.isEmpty()) {
            throw new RegraNegocioException("O nome da fase não pode estar em branco.");
        }
        if (nomeTrimmed.length() < TAMANHO_MINIMO_NOME) {
            throw new RegraNegocioException("O nome da fase deve ter pelo menos " + TAMANHO_MINIMO_NOME + " caracteres.");
        }
        if (nomeTrimmed.length() > TAMANHO_MAXIMO_NOME) {
            throw new RegraNegocioException("O nome da fase deve ter no máximo " + TAMANHO_MAXIMO_NOME + " caracteres.");
        }
        if (fase.getOrdem() == null || fase.getOrdem() < 1) {
            throw new RegraNegocioException("A ordem da fase é obrigatória e deve ser maior ou igual a 1.");
        }
        if (fase.getEhFaseFinal() == null) {
            throw new RegraNegocioException("A indicação de fase final é obrigatória.");
        }
    }

    public Projeto buscarProjetoVinculado(Projeto projeto) {
        if (projeto == null || projeto.getId() == null) {
            throw new RegraNegocioException("O projeto da fase é obrigatório.");
        }
        return projetoRepository.findById(projeto.getId())
                .orElseThrow(() -> new RegraNegocioException("Projeto informado não encontrado."));
    }

    public void validarLimitesOrdem(Integer ordem, int limiteMaximo) {
        if (ordem < 1 || ordem > limiteMaximo) {
            throw new RegraNegocioException(
                    "A ordem da fase deve estar entre 1 e " + limiteMaximo + "."
            );
        }
    }

    public void validarOrdemUnica(Long faseId, Long projetoId, Integer ordem) {
        boolean ordemDuplicada;
        if (faseId == null) {
            ordemDuplicada = faseRepository.existsByProjetoIdAndOrdem(projetoId, ordem);
        } else {
            ordemDuplicada = faseRepository.existsByProjetoIdAndOrdemAndIdNot(projetoId, ordem, faseId);
        }

        if (ordemDuplicada) {
            throw new RegraNegocioException("Já existe uma fase com essa ordem neste projeto.");
        }
    }

    public Fase trocarOrdemComVizinha(Long faseId, int deslocamento) {
        Fase atual = buscarPorId(faseId);
        Long projetoId = atual.getProjeto().getId();
        List<Fase> fasesOrdenadas = faseRepository.findByProjetoIdOrderByOrdemAsc(projetoId);

        int indiceAtual = -1;
        for (int i = 0; i < fasesOrdenadas.size(); i++) {
            if (fasesOrdenadas.get(i).getId().equals(atual.getId())) {
                indiceAtual = i;
                break;
            }
        }

        if (indiceAtual == -1) {
            throw new RegraNegocioException("Fase não encontrada no projeto.");
        }

        int indiceVizinha = indiceAtual + deslocamento;
        if (indiceVizinha < 0 || indiceVizinha >= fasesOrdenadas.size()) {
            throw new RegraNegocioException("Não há fase adjacente disponível nessa direção.");
        }

        Fase vizinha = fasesOrdenadas.get(indiceVizinha);
        Integer ordemAtual = atual.getOrdem();
        Integer ordemVizinha = vizinha.getOrdem();

        atual.setOrdem(-1);
        faseRepository.saveAndFlush(atual);

        vizinha.setOrdem(ordemAtual);
        faseRepository.saveAndFlush(vizinha);

        atual.setOrdem(ordemVizinha);
        return faseRepository.saveAndFlush(atual);
    }
}