package br.edu.ifsudestemg.kb.service;

import br.edu.ifsudestemg.kb.exception.RegraNegocioException;
import br.edu.ifsudestemg.kb.model.entity.Funcionario;
import br.edu.ifsudestemg.kb.model.entity.FuncionarioProjeto;
import br.edu.ifsudestemg.kb.model.entity.Projeto;
import br.edu.ifsudestemg.kb.model.repository.FuncionarioProjetoRepository;
import br.edu.ifsudestemg.kb.model.repository.FuncionarioRepository;
import br.edu.ifsudestemg.kb.model.repository.ProjetoRepository;
import br.edu.ifsudestemg.kb.model.repository.ResponsavelTarefaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FuncionarioProjetoService {

    private final FuncionarioProjetoRepository funcionarioProjetoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final ProjetoRepository projetoRepository;
    private final ResponsavelTarefaRepository responsavelTarefaRepository;

    public FuncionarioProjetoService(
            FuncionarioProjetoRepository funcionarioProjetoRepository,
            FuncionarioRepository funcionarioRepository,
            ProjetoRepository projetoRepository,
            ResponsavelTarefaRepository responsavelTarefaRepository
    ) {
        this.funcionarioProjetoRepository = funcionarioProjetoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.projetoRepository = projetoRepository;
        this.responsavelTarefaRepository = responsavelTarefaRepository;
    }

    public List<FuncionarioProjeto> listarPorProjeto(Long projetoId) {
        return funcionarioProjetoRepository.findByProjetoId(projetoId);
    }

    public List<FuncionarioProjeto> listarPorFuncionario(Long funcionarioId) {
        return funcionarioProjetoRepository.findByFuncionarioId(funcionarioId);
    }

    public FuncionarioProjeto buscarPorId(Long id) {
        return funcionarioProjetoRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Vínculo de funcionário no projeto não encontrado."));
    }

    @Transactional
    public FuncionarioProjeto salvar(FuncionarioProjeto vinculo) {
        validar(vinculo);
        Funcionario funcionario = buscarFuncionarioVinculado(vinculo.getFuncionario());
        Projeto projeto = buscarProjetoVinculado(vinculo.getProjeto());
        validarDuplicidadeVinculo(funcionario.getId(), projeto.getId(), null);

        vinculo.setFuncionario(funcionario);
        vinculo.setProjeto(projeto);
        return funcionarioProjetoRepository.save(vinculo);
    }

    @Transactional
    public FuncionarioProjeto atualizar(Long id, FuncionarioProjeto dados) {
        FuncionarioProjeto existente = buscarPorId(id);
        validar(dados);

        Funcionario funcionario;
        if (dados.getFuncionario() != null) {
            funcionario = buscarFuncionarioVinculado(dados.getFuncionario());
        } else {
            funcionario = existente.getFuncionario();
        }

        Projeto projeto;
        if (dados.getProjeto() != null) {
            projeto = buscarProjetoVinculado(dados.getProjeto());
        } else {
            projeto = existente.getProjeto();
        }

        validarDuplicidadeVinculo(funcionario.getId(), projeto.getId(), id);

        existente.setEhResponsavel(dados.getEhResponsavel());
        existente.setFuncionario(funcionario);
        existente.setProjeto(projeto);
        return funcionarioProjetoRepository.save(existente);
    }

    @Transactional
    public void excluir(Long id) {
        FuncionarioProjeto existente = buscarPorId(id);
        responsavelTarefaRepository.deleteByFuncionarioProjetoId(id);
        funcionarioProjetoRepository.delete(existente);
    }

    public void validar(FuncionarioProjeto vinculo) {
        if (vinculo == null) {
            throw new RegraNegocioException("Dados do vínculo são obrigatórios.");
        }
        if (vinculo.getEhResponsavel() == null) {
            throw new RegraNegocioException("A indicação de responsável do projeto é obrigatória.");
        }
    }

    public Funcionario buscarFuncionarioVinculado(Funcionario funcionario) {
        if (funcionario == null || funcionario.getId() == null) {
            throw new RegraNegocioException("O funcionário do vínculo é obrigatório.");
        }
        return funcionarioRepository.findById(funcionario.getId())
                .orElseThrow(() -> new RegraNegocioException("Funcionário informado não encontrado."));
    }

    public Projeto buscarProjetoVinculado(Projeto projeto) {
        if (projeto == null || projeto.getId() == null) {
            throw new RegraNegocioException("O projeto do vínculo é obrigatório.");
        }
        return projetoRepository.findById(projeto.getId())
                .orElseThrow(() -> new RegraNegocioException("Projeto informado não encontrado."));
    }

    public void validarDuplicidadeVinculo(Long funcionarioId, Long projetoId, Long idAtual) {
        List<FuncionarioProjeto> vinculosDoProjeto = funcionarioProjetoRepository.findByProjetoId(projetoId);
        for (FuncionarioProjeto item : vinculosDoProjeto) {
            boolean mesmoFuncionario = item.getFuncionario() != null
                    && item.getFuncionario().getId() != null
                    && item.getFuncionario().getId().equals(funcionarioId);
            boolean mesmoRegistro = idAtual != null && idAtual.equals(item.getId());
            if (mesmoFuncionario && !mesmoRegistro) {
                throw new RegraNegocioException("Este funcionário já está vinculado a este projeto.");
            }
        }
    }
}