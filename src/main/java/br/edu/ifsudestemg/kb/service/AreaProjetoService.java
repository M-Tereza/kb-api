package br.edu.ifsudestemg.kb.service;

import br.edu.ifsudestemg.kb.exception.RegraNegocioException;
import br.edu.ifsudestemg.kb.model.entity.AreaProjeto;
import br.edu.ifsudestemg.kb.model.repository.AreaProjetoRepository;
import br.edu.ifsudestemg.kb.model.repository.ProjetoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AreaProjetoService {

    private static final int TAMANHO_MINIMO_NOME = 2;
    private static final int TAMANHO_MAXIMO_NOME = 100;

    private final AreaProjetoRepository areaProjetoRepository;
    private final ProjetoRepository projetoRepository;

    public AreaProjetoService(AreaProjetoRepository areaProjetoRepository, ProjetoRepository projetoRepository) {
        this.areaProjetoRepository = areaProjetoRepository;
        this.projetoRepository = projetoRepository;
    }

    public List<AreaProjeto> listarTodas() {
        return areaProjetoRepository.findAll();
    }

    public AreaProjeto buscarPorId(Long id) {
        return areaProjetoRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Área de projeto não encontrada."));
    }

    @Transactional
    public AreaProjeto salvar(AreaProjeto areaProjeto) {
        validar(areaProjeto);
        areaProjeto.setNome(areaProjeto.getNome().trim());
        return areaProjetoRepository.save(areaProjeto);
    }

    @Transactional
    public AreaProjeto atualizar(Long id, AreaProjeto dados) {
        AreaProjeto existente = buscarPorId(id);
        validar(dados);
        existente.setNome(dados.getNome().trim());
        return areaProjetoRepository.save(existente);
    }

    @Transactional
    public void excluir(Long id) {
        AreaProjeto existente = buscarPorId(id);
        if (!projetoRepository.findByAreaProjetoId(id).isEmpty()) {
            throw new RegraNegocioException("Não é possível excluir área de projeto vinculada a projetos.");
        }
        areaProjetoRepository.delete(existente);
    }

    public void validar(AreaProjeto areaProjeto) {
        if (areaProjeto == null) {
            throw new RegraNegocioException("Dados da área de projeto são obrigatórios.");
        }
        if (areaProjeto.getNome() == null) {
            throw new RegraNegocioException("O nome da área de projeto é obrigatório.");
        }
        String nomeTrimmed = areaProjeto.getNome().trim();
        if (nomeTrimmed.isEmpty()) {
            throw new RegraNegocioException("O nome da área de projeto não pode estar em branco.");
        }
        if (nomeTrimmed.length() < TAMANHO_MINIMO_NOME) {
            throw new RegraNegocioException("O nome da área de projeto deve ter pelo menos " + TAMANHO_MINIMO_NOME + " caracteres.");
        }
        if (nomeTrimmed.length() > TAMANHO_MAXIMO_NOME) {
            throw new RegraNegocioException("O nome da área de projeto deve ter no máximo " + TAMANHO_MAXIMO_NOME + " caracteres.");
        }
    }
}