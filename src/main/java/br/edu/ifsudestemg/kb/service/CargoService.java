package br.edu.ifsudestemg.kb.service;

import br.edu.ifsudestemg.kb.exception.RegraNegocioException;
import br.edu.ifsudestemg.kb.model.entity.Cargo;
import br.edu.ifsudestemg.kb.model.repository.CargoRepository;
import br.edu.ifsudestemg.kb.model.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CargoService {

    private static final int TAMANHO_MINIMO_NOME = 2;
    private static final int TAMANHO_MAXIMO_NOME = 100;

    private final CargoRepository cargoRepository;
    private final FuncionarioRepository funcionarioRepository;

    public CargoService(CargoRepository cargoRepository, FuncionarioRepository funcionarioRepository) {
        this.cargoRepository = cargoRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    public List<Cargo> listarTodos() {
        return cargoRepository.findAll();
    }

    public Cargo buscarPorId(Long id) {
        return cargoRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Cargo não encontrado."));
    }

    @Transactional
    public Cargo salvar(Cargo cargo) {
        validar(cargo);
        cargo.setNome(cargo.getNome().trim());
        return cargoRepository.save(cargo);
    }

    @Transactional
    public Cargo atualizar(Long id, Cargo dados) {
        Cargo existente = buscarPorId(id);
        validar(dados);
        existente.setNome(dados.getNome().trim());
        return cargoRepository.save(existente);
    }

    @Transactional
    public void excluir(Long id) {
        Cargo existente = buscarPorId(id);
        if (!funcionarioRepository.findByCargoId(id).isEmpty()) {
            throw new RegraNegocioException("Não é possível excluir cargo vinculado a funcionários.");
        }
        cargoRepository.delete(existente);
    }

    public void validar(Cargo cargo) {
        if (cargo == null) {
            throw new RegraNegocioException("Dados do cargo são obrigatórios.");
        }
        if (cargo.getNome() == null) {
            throw new RegraNegocioException("O nome do cargo é obrigatório.");
        }
        String nomeTrimmed = cargo.getNome().trim();
        if (nomeTrimmed.isEmpty()) {
            throw new RegraNegocioException("O nome do cargo não pode estar em branco.");
        }
        if (nomeTrimmed.length() < TAMANHO_MINIMO_NOME) {
            throw new RegraNegocioException("O nome do cargo deve ter pelo menos " + TAMANHO_MINIMO_NOME + " caracteres.");
        }
        if (nomeTrimmed.length() > TAMANHO_MAXIMO_NOME) {
            throw new RegraNegocioException("O nome do cargo deve ter no máximo " + TAMANHO_MAXIMO_NOME + " caracteres.");
        }
    }
}