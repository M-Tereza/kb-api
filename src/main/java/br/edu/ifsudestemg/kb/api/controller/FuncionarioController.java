package br.edu.ifsudestemg.kb.api.controller;

import br.edu.ifsudestemg.kb.api.dto.FuncionarioDTO;
import br.edu.ifsudestemg.kb.exception.RegraNegocioException;
import br.edu.ifsudestemg.kb.model.entity.Cargo;
import br.edu.ifsudestemg.kb.model.entity.Funcionario;
import br.edu.ifsudestemg.kb.service.FuncionarioService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/funcionarios")
@RequiredArgsConstructor
@CrossOrigin
public class FuncionarioController {

    private final FuncionarioService service;

    @GetMapping
    @ApiOperation("Listar funcionários")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Funcionários listados com sucesso")
    })
    public ResponseEntity get() {
        List<FuncionarioDTO> dtos = service.listarTodos()
                .stream()
                .map(FuncionarioDTO::create)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @ApiOperation("Buscar funcionário e perfil por ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Funcionário encontrado"),
            @ApiResponse(code = 404, message = "Funcionário não encontrado")
    })
    public ResponseEntity get(@PathVariable("id") Long id) {
        try {
            Funcionario funcionario = service.buscarPorId(id);
            return ResponseEntity.ok(FuncionarioDTO.create(funcionario));
        } catch (RegraNegocioException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @ApiOperation("Cadastrar funcionário")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Funcionário cadastrado com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação ou regra de negócio")
    })
    public ResponseEntity post(@RequestBody FuncionarioDTO dto) {
        try {
            Funcionario entidade = converter(dto);
            Funcionario salvo = service.salvar(entidade);
            return new ResponseEntity(FuncionarioDTO.create(salvo), HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ApiOperation("Atualizar funcionário e perfil")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Funcionário atualizado com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação ou regra de negócio"),
            @ApiResponse(code = 404, message = "Funcionário não encontrado")
    })
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody FuncionarioDTO dto) {
        try {
            Funcionario entidade = converter(dto);
            Funcionario atualizado = service.atualizar(id, entidade);
            return ResponseEntity.ok(FuncionarioDTO.create(atualizado));
        } catch (RegraNegocioException e) {
            if ("Funcionário não encontrado.".equals(e.getMessage())) {
                return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Excluir funcionário")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Funcionário excluído com sucesso"),
            @ApiResponse(code = 404, message = "Funcionário não encontrado")
    })
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        try {
            service.excluir(id);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            if ("Funcionário não encontrado.".equals(e.getMessage())) {
                return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private Funcionario converter(FuncionarioDTO dto) {
        if (dto == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        Funcionario entidade = modelMapper.map(dto, Funcionario.class);
        entidade.setId(null);
        if (dto.getIdCargo() != null) {
            Cargo cargo = new Cargo();
            cargo.setId(dto.getIdCargo());
            entidade.setCargo(cargo);
        } else {
            entidade.setCargo(null);
        }
        return entidade;
    }
}