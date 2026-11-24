package br.edu.ifsudestemg.kb.api.controller;

import br.edu.ifsudestemg.kb.api.dto.FuncionarioProjetoDTO;
import br.edu.ifsudestemg.kb.exception.RegraNegocioException;
import br.edu.ifsudestemg.kb.model.entity.Funcionario;
import br.edu.ifsudestemg.kb.model.entity.FuncionarioProjeto;
import br.edu.ifsudestemg.kb.model.entity.Projeto;
import br.edu.ifsudestemg.kb.service.FuncionarioProjetoService;
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
@RequestMapping("/api/v1/funcionarios-projeto")
@RequiredArgsConstructor
@CrossOrigin
public class FuncionarioProjetoController {

    private final FuncionarioProjetoService service;

    @GetMapping("/projeto/{projetoId}")
    @ApiOperation("Listar vínculos de funcionários de um projeto")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Vínculos do projeto listados com sucesso")
    })
    public ResponseEntity listarPorProjeto(@PathVariable("projetoId") Long projetoId) {
        List<FuncionarioProjetoDTO> dtos = service.listarPorProjeto(projetoId)
                .stream()
                .map(FuncionarioProjetoDTO::create)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/funcionario/{funcionarioId}")
    @ApiOperation("Listar vínculos de projetos de um funcionário")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Vínculos do funcionário listados com sucesso")
    })
    public ResponseEntity listarPorFuncionario(@PathVariable("funcionarioId") Long funcionarioId) {
        List<FuncionarioProjetoDTO> dtos = service.listarPorFuncionario(funcionarioId)
                .stream()
                .map(FuncionarioProjetoDTO::create)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @ApiOperation("Buscar vínculo de funcionário no projeto por ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Vínculo encontrado"),
            @ApiResponse(code = 404, message = "Vínculo não encontrado")
    })
    public ResponseEntity get(@PathVariable("id") Long id) {
        try {
            FuncionarioProjeto vinculo = service.buscarPorId(id);
            return ResponseEntity.ok(FuncionarioProjetoDTO.create(vinculo));
        } catch (RegraNegocioException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @ApiOperation("Vincular funcionário a projeto")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Vínculo criado com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação, duplicidade ou regra de negócio")
    })
    public ResponseEntity post(@RequestBody FuncionarioProjetoDTO dto) {
        try {
            FuncionarioProjeto entidade = converter(dto);
            FuncionarioProjeto salvo = service.salvar(entidade);
            return new ResponseEntity(FuncionarioProjetoDTO.create(salvo), HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ApiOperation("Atualizar vínculo de funcionário no projeto")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Vínculo atualizado com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação, duplicidade ou regra de negócio"),
            @ApiResponse(code = 404, message = "Vínculo não encontrado")
    })
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody FuncionarioProjetoDTO dto) {
        try {
            FuncionarioProjeto entidade = converter(dto);
            FuncionarioProjeto atualizado = service.atualizar(id, entidade);
            return ResponseEntity.ok(FuncionarioProjetoDTO.create(atualizado));
        } catch (RegraNegocioException e) {
            if ("Vínculo de funcionário no projeto não encontrado.".equals(e.getMessage())) {
                return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Excluir vínculo de funcionário no projeto")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Vínculo excluído com sucesso"),
            @ApiResponse(code = 404, message = "Vínculo não encontrado")
    })
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        try {
            service.excluir(id);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            if ("Vínculo de funcionário no projeto não encontrado.".equals(e.getMessage())) {
                return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private FuncionarioProjeto converter(FuncionarioProjetoDTO dto) {
        if (dto == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        FuncionarioProjeto entidade = modelMapper.map(dto, FuncionarioProjeto.class);
        entidade.setId(null);

        if (dto.getIdFuncionario() != null) {
            Funcionario funcionario = new Funcionario();
            funcionario.setId(dto.getIdFuncionario());
            entidade.setFuncionario(funcionario);
        } else {
            entidade.setFuncionario(null);
        }

        if (dto.getIdProjeto() != null) {
            Projeto projeto = new Projeto();
            projeto.setId(dto.getIdProjeto());
            entidade.setProjeto(projeto);
        } else {
            entidade.setProjeto(null);
        }

        return entidade;
    }
}