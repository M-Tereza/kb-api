package br.edu.ifsudestemg.kb.service;

import br.edu.ifsudestemg.kb.exception.RegraNegocioException;
import br.edu.ifsudestemg.kb.model.entity.Etiqueta;
import br.edu.ifsudestemg.kb.model.entity.Projeto;
import br.edu.ifsudestemg.kb.model.repository.EtiquetaRepository;
import br.edu.ifsudestemg.kb.model.repository.ProjetoRepository;
import br.edu.ifsudestemg.kb.model.repository.TarefaEtiquetaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EtiquetaService {

    private static final int TAMANHO_MINIMO_NOME = 2;
    private static final int TAMANHO_MAXIMO_NOME = 80;
    private static final int TAMANHO_MAXIMO_COR = 30;

    private final EtiquetaRepository etiquetaRepository;
    private final ProjetoRepository projetoRepository;
    private final TarefaEtiquetaRepository tarefaEtiquetaRepository;

    public EtiquetaService(
            EtiquetaRepository etiquetaRepository,
            ProjetoRepository projetoRepository,
            TarefaEtiquetaRepository tarefaEtiquetaRepository
    ) {
        this.etiquetaRepository = etiquetaRepository;
        this.projetoRepository = projetoRepository;
        this.tarefaEtiquetaRepository = tarefaEtiquetaRepository;
    }

    public List<Etiqueta> listarPorProjeto(Long projetoId) {
        return etiquetaRepository.findByProjetoId(projetoId);
    }

    public Etiqueta buscarPorId(Long id) {
        return etiquetaRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Etiqueta não encontrada."));
    }

    @Transactional
    public Etiqueta salvar(Etiqueta etiqueta) {
        validar(etiqueta);
        Projeto projeto = buscarProjetoVinculado(etiqueta.getProjeto());

        etiqueta.setNome(etiqueta.getNome().trim());
        etiqueta.setCor(trimOuNulo(etiqueta.getCor()));
        etiqueta.setProjeto(projeto);
        return etiquetaRepository.save(etiqueta);
    }

    @Transactional
    public Etiqueta atualizar(Long id, Etiqueta dados) {
        Etiqueta existente = buscarPorId(id);
        validar(dados);

        Projeto projeto;
        if (dados.getProjeto() != null) {
            projeto = buscarProjetoVinculado(dados.getProjeto());
        } else {
            projeto = existente.getProjeto();
        }

        existente.setNome(dados.getNome().trim());
        existente.setCor(trimOuNulo(dados.getCor()));
        existente.setProjeto(projeto);
        return etiquetaRepository.save(existente);
    }

    @Transactional
    public void excluir(Long id) {
        Etiqueta existente = buscarPorId(id);
        tarefaEtiquetaRepository.deleteByEtiquetaId(id);
        etiquetaRepository.delete(existente);
    }

    public void validar(Etiqueta etiqueta) {
        if (etiqueta == null) {
            throw new RegraNegocioException("Dados da etiqueta são obrigatórios.");
        }
        if (etiqueta.getNome() == null) {
            throw new RegraNegocioException("O nome da etiqueta é obrigatório.");
        }
        String nomeTrimmed = etiqueta.getNome().trim();
        if (nomeTrimmed.isEmpty()) {
            throw new RegraNegocioException("O nome da etiqueta não pode estar em branco.");
        }
        if (nomeTrimmed.length() < TAMANHO_MINIMO_NOME) {
            throw new RegraNegocioException("O nome da etiqueta deve ter pelo menos " + TAMANHO_MINIMO_NOME + " caracteres.");
        }
        if (nomeTrimmed.length() > TAMANHO_MAXIMO_NOME) {
            throw new RegraNegocioException("O nome da etiqueta deve ter no máximo " + TAMANHO_MAXIMO_NOME + " caracteres.");
        }
        if (etiqueta.getCor() != null && etiqueta.getCor().trim().length() > TAMANHO_MAXIMO_COR) {
            throw new RegraNegocioException("A cor da etiqueta deve ter no máximo " + TAMANHO_MAXIMO_COR + " caracteres.");
        }
    }

    public Projeto buscarProjetoVinculado(Projeto projeto) {
        if (projeto == null || projeto.getId() == null) {
            throw new RegraNegocioException("O projeto da etiqueta é obrigatório.");
        }
        return projetoRepository.findById(projeto.getId())
                .orElseThrow(() -> new RegraNegocioException("Projeto informado não encontrado."));
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
}