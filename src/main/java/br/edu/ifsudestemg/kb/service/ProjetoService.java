package br.edu.ifsudestemg.kb.service;

import br.edu.ifsudestemg.kb.exception.RegraNegocioException;
import br.edu.ifsudestemg.kb.model.entity.AreaProjeto;
import br.edu.ifsudestemg.kb.model.entity.Fase;
import br.edu.ifsudestemg.kb.model.entity.Projeto;
import br.edu.ifsudestemg.kb.model.repository.AreaProjetoRepository;
import br.edu.ifsudestemg.kb.model.repository.EtiquetaRepository;
import br.edu.ifsudestemg.kb.model.repository.FaseRepository;
import br.edu.ifsudestemg.kb.model.repository.FuncionarioProjetoRepository;
import br.edu.ifsudestemg.kb.model.repository.ProjetoRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjetoService {

    private static final int TAMANHO_MINIMO_TITULO = 2;
    private static final int TAMANHO_MAXIMO_TITULO = 120;
    private static final int TAMANHO_MAXIMO_DESCRICAO = 500;

    private final ProjetoRepository projetoRepository;
    private final AreaProjetoRepository areaProjetoRepository;
    private final FaseRepository faseRepository;
    private final EtiquetaRepository etiquetaRepository;
    private final FuncionarioProjetoRepository funcionarioProjetoRepository;
    private final FaseService faseService;

    public ProjetoService(
            ProjetoRepository projetoRepository,
            AreaProjetoRepository areaProjetoRepository,
            FaseRepository faseRepository,
            EtiquetaRepository etiquetaRepository,
            FuncionarioProjetoRepository funcionarioProjetoRepository,
            @Lazy FaseService faseService
    ) {
        this.projetoRepository = projetoRepository;
        this.areaProjetoRepository = areaProjetoRepository;
        this.faseRepository = faseRepository;
        this.etiquetaRepository = etiquetaRepository;
        this.funcionarioProjetoRepository = funcionarioProjetoRepository;
        this.faseService = faseService;
    }

    public List<Projeto> listarTodos() {
        return projetoRepository.findAll();
    }

    public Projeto buscarPorId(Long id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Projeto não encontrado."));
    }

    @Transactional
    public Projeto salvar(Projeto projeto) {
        validar(projeto);
        AreaProjeto areaProjeto = buscarAreaVinculada(projeto.getAreaProjeto());

        projeto.setTitulo(projeto.getTitulo().trim());
        projeto.setDescricao(trimOuNulo(projeto.getDescricao()));
        projeto.setAreaProjeto(areaProjeto);

        Projeto salvo = projetoRepository.save(projeto);
        criarFasesPadrao(salvo);
        return salvo;
    }

    @Transactional
    public Projeto atualizar(Long id, Projeto dados) {
        Projeto existente = buscarPorId(id);
        validar(dados);
        AreaProjeto areaProjeto = buscarAreaVinculada(dados.getAreaProjeto());

        existente.setTitulo(dados.getTitulo().trim());
        existente.setDescricao(trimOuNulo(dados.getDescricao()));
        existente.setAreaProjeto(areaProjeto);
        return projetoRepository.save(existente);
    }

    @Transactional
    public void excluir(Long id) {
        Projeto existente = buscarPorId(id);

        List<Fase> fases = faseRepository.findByProjetoIdOrderByOrdemAsc(id);
        for (Fase fase : fases) {
            faseService.excluir(fase.getId(), true);
        }

        etiquetaRepository.deleteByProjetoId(id);
        funcionarioProjetoRepository.deleteByProjetoId(id);
        projetoRepository.delete(existente);
    }

    public void validar(Projeto projeto) {
        if (projeto == null) {
            throw new RegraNegocioException("Dados do projeto são obrigatórios.");
        }
        if (projeto.getTitulo() == null) {
            throw new RegraNegocioException("O título do projeto é obrigatório.");
        }
        String tituloTrimmed = projeto.getTitulo().trim();
        if (tituloTrimmed.isEmpty()) {
            throw new RegraNegocioException("O título do projeto não pode estar em branco.");
        }
        if (tituloTrimmed.length() < TAMANHO_MINIMO_TITULO) {
            throw new RegraNegocioException("O título do projeto deve ter pelo menos " + TAMANHO_MINIMO_TITULO + " caracteres.");
        }
        if (tituloTrimmed.length() > TAMANHO_MAXIMO_TITULO) {
            throw new RegraNegocioException("O título do projeto deve ter no máximo " + TAMANHO_MAXIMO_TITULO + " caracteres.");
        }
        if (projeto.getDescricao() != null && projeto.getDescricao().trim().length() > TAMANHO_MAXIMO_DESCRICAO) {
            throw new RegraNegocioException("A descrição do projeto deve ter no máximo " + TAMANHO_MAXIMO_DESCRICAO + " caracteres.");
        }
    }

    public AreaProjeto buscarAreaVinculada(AreaProjeto areaProjeto) {
        if (areaProjeto == null || areaProjeto.getId() == null) {
            return null;
        }
        return areaProjetoRepository.findById(areaProjeto.getId())
                .orElseThrow(() -> new RegraNegocioException("Área de projeto informada não encontrada."));
    }

    public String trimOuNulo(String valor) {
        if (valor != null) {
            String trimmed = valor.trim();
            if (trimmed.isEmpty()) {
                return null;
            }
            return trimmed;
        } else {
            return null;
        }
    }

    public void criarFasesPadrao(Projeto projeto) {
        faseRepository.save(new Fase(null, "Backlog", 1, false, projeto));
        faseRepository.save(new Fase(null, "Fazendo", 2, false, projeto));
        faseRepository.save(new Fase(null, "Pausado", 3, false, projeto));
        faseRepository.save(new Fase(null, "Concluído", 4, true, projeto));
        faseRepository.save(new Fase(null, "Arquivado", 5, true, projeto));
    }
}