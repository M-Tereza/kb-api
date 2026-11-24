package br.edu.ifsudestemg.kb.service;

import br.edu.ifsudestemg.kb.exception.RegraNegocioException;
import br.edu.ifsudestemg.kb.model.entity.Cargo;
import br.edu.ifsudestemg.kb.model.entity.Funcionario;
import br.edu.ifsudestemg.kb.model.entity.FuncionarioProjeto;
import br.edu.ifsudestemg.kb.model.repository.CargoRepository;
import br.edu.ifsudestemg.kb.model.repository.FuncionarioProjetoRepository;
import br.edu.ifsudestemg.kb.model.repository.FuncionarioRepository;
import br.edu.ifsudestemg.kb.model.repository.ResponsavelTarefaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class FuncionarioService {

    private static final int TAMANHO_MINIMO_NOME = 2;
    private static final int TAMANHO_MAXIMO_NOME = 120;
    private static final int TAMANHO_MAXIMO_EMAIL = 150;
    private static final int TAMANHO_MAXIMO_TELEFONE = 20;
    private static final int TAMANHO_MAXIMO_BIO = 300;

    private final FuncionarioRepository funcionarioRepository;
    private final CargoRepository cargoRepository;
    private final FuncionarioProjetoRepository funcionarioProjetoRepository;
    private final ResponsavelTarefaRepository responsavelTarefaRepository;

    public FuncionarioService(
            FuncionarioRepository funcionarioRepository,
            CargoRepository cargoRepository,
            FuncionarioProjetoRepository funcionarioProjetoRepository,
            ResponsavelTarefaRepository responsavelTarefaRepository
    ) {
        this.funcionarioRepository = funcionarioRepository;
        this.cargoRepository = cargoRepository;
        this.funcionarioProjetoRepository = funcionarioProjetoRepository;
        this.responsavelTarefaRepository = responsavelTarefaRepository;
    }

    public List<Funcionario> listarTodos() {
        return funcionarioRepository.findAll();
    }

    public Funcionario buscarPorId(Long id) {
        return funcionarioRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Funcionário não encontrado."));
    }

    @Transactional
    public Funcionario salvar(Funcionario funcionario) {
        validar(funcionario);
        Cargo cargo = buscarCargoVinculado(funcionario.getCargo());
        funcionario.setNome(funcionario.getNome().trim());
        funcionario.setEmail(funcionario.getEmail().trim());
        funcionario.setTelefone(trimOuNulo(funcionario.getTelefone()));
        funcionario.setBio(trimOuNulo(funcionario.getBio()));
        funcionario.setCargo(cargo);
        return funcionarioRepository.save(funcionario);
    }

    @Transactional
    public Funcionario atualizar(Long id, Funcionario dados) {
        Funcionario existente = buscarPorId(id);
        validar(dados);
        Cargo cargo = buscarCargoVinculado(dados.getCargo());

        existente.setNome(dados.getNome().trim());
        existente.setEmail(dados.getEmail().trim());
        existente.setDataNascimento(dados.getDataNascimento());
        existente.setTelefone(trimOuNulo(dados.getTelefone()));
        existente.setBio(trimOuNulo(dados.getBio()));
        existente.setCargo(cargo);
        return funcionarioRepository.save(existente);
    }

    @Transactional
    public void excluir(Long id) {
        Funcionario existente = buscarPorId(id);
        List<FuncionarioProjeto> participacoes = funcionarioProjetoRepository.findByFuncionarioId(id);
        for (FuncionarioProjeto participacao : participacoes) {
            responsavelTarefaRepository.deleteByFuncionarioProjetoId(participacao.getId());
        }
        funcionarioProjetoRepository.deleteAll(participacoes);
        funcionarioRepository.delete(existente);
    }

    public void validar(Funcionario funcionario) {
        if (funcionario == null) {
            throw new RegraNegocioException("Dados do funcionário são obrigatórios.");
        }
        if (funcionario.getNome() == null) {
            throw new RegraNegocioException("O nome do funcionário é obrigatório.");
        }
        String nomeTrimmed = funcionario.getNome().trim();
        if (nomeTrimmed.isEmpty()) {
            throw new RegraNegocioException("O nome do funcionário não pode estar em branco.");
        }
        if (nomeTrimmed.length() < TAMANHO_MINIMO_NOME) {
            throw new RegraNegocioException("O nome do funcionário deve ter pelo menos " + TAMANHO_MINIMO_NOME + " caracteres.");
        }
        if (nomeTrimmed.length() > TAMANHO_MAXIMO_NOME) {
            throw new RegraNegocioException("O nome do funcionário deve ter no máximo " + TAMANHO_MAXIMO_NOME + " caracteres.");
        }
        if (funcionario.getEmail() == null) {
            throw new RegraNegocioException("O e-mail do funcionário é obrigatório.");
        }
        String emailTrimmed = funcionario.getEmail().trim();
        if (emailTrimmed.isEmpty()) {
            throw new RegraNegocioException("O e-mail do funcionário não pode estar em branco.");
        }
        if (!emailTrimmed.contains("@") || !emailTrimmed.contains(".")) {
            throw new RegraNegocioException("O e-mail do funcionário deve ter um formato válido.");
        }
        if (emailTrimmed.length() > TAMANHO_MAXIMO_EMAIL) {
            throw new RegraNegocioException("O e-mail do funcionário deve ter no máximo " + TAMANHO_MAXIMO_EMAIL + " caracteres.");
        }
        if (funcionario.getDataNascimento() != null && funcionario.getDataNascimento().isAfter(LocalDate.now())) {
            throw new RegraNegocioException("A data de nascimento não pode ser futura.");
        }
        if (funcionario.getTelefone() != null && funcionario.getTelefone().trim().length() > TAMANHO_MAXIMO_TELEFONE) {
            throw new RegraNegocioException("O telefone do funcionário deve ter no máximo " + TAMANHO_MAXIMO_TELEFONE + " caracteres.");
        }
        if (funcionario.getBio() != null && funcionario.getBio().trim().length() > TAMANHO_MAXIMO_BIO) {
            throw new RegraNegocioException("A biografia do funcionário deve ter no máximo " + TAMANHO_MAXIMO_BIO + " caracteres.");
        }
    }

    public Cargo buscarCargoVinculado(Cargo cargo) {
        if (cargo == null || cargo.getId() == null) {
            return null;
        }
        return cargoRepository.findById(cargo.getId())
                .orElseThrow(() -> new RegraNegocioException("Cargo informado não encontrado."));
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