package br.edu.ifsudestemg.kb.api.controller;

import br.edu.ifsudestemg.kb.api.dto.ResponsavelTarefaDTO;
import br.edu.ifsudestemg.kb.exception.RegraNegocioException;
import br.edu.ifsudestemg.kb.model.entity.FuncionarioProjeto;
import br.edu.ifsudestemg.kb.model.entity.ResponsavelTarefa;
import br.edu.ifsudestemg.kb.model.entity.Tarefa;
import br.edu.ifsudestemg.kb.service.ResponsavelTarefaService;
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
@RequestMapping("/api/v1/responsaveis-tarefa")
@RequiredArgsConstructor
@CrossOrigin
public class ResponsavelTarefaController {

    private final ResponsavelTarefaService service;

    @GetMapping("/tarefa/{tarefaId}")
    @ApiOperation("Listar responsáveis de uma tarefa")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Responsáveis da tarefa listados com sucesso")
    })
    public ResponseEntity listarPorTarefa(@PathVariable("tarefaId") Long tarefaId) {
        List<ResponsavelTarefaDTO> dtos = service.listarPorTarefa(tarefaId)
                .stream()
                .map(ResponsavelTarefaDTO::create)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/funcionario-projeto/{funcionarioProjetoId}")
    @ApiOperation("Listar atribuições de tarefas de um vínculo de funcionário no projeto")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Atribuições listadas com sucesso")
    })
    public ResponseEntity listarPorFuncionarioProjeto(@PathVariable("funcionarioProjetoId") Long funcionarioProjetoId) {
        List<ResponsavelTarefaDTO> dtos = service.listarPorFuncionarioProjeto(funcionarioProjetoId)
                .stream()
                .map(ResponsavelTarefaDTO::create)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @ApiOperation("Buscar atribuição de responsável por ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Atribuição encontrada"),
            @ApiResponse(code = 404, message = "Atribuição de responsável não encontrada")
    })
    public ResponseEntity get(@PathVariable("id") Long id) {
        try {
            ResponsavelTarefa responsavel = service.buscarPorId(id);
            return ResponseEntity.ok(ResponsavelTarefaDTO.create(responsavel));
        } catch (RegraNegocioException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @ApiOperation("Atribuir responsável a tarefa")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Responsável atribuído com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação ou projetos incompatíveis")
    })
    public ResponseEntity post(@RequestBody ResponsavelTarefaDTO dto) {
        try {
            ResponsavelTarefa entidade = converter(dto);
            ResponsavelTarefa salvo = service.salvar(entidade);
            return new ResponseEntity(ResponsavelTarefaDTO.create(salvo), HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ApiOperation("Atualizar responsável de tarefa")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Atribuição atualizada com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação ou projetos incompatíveis"),
            @ApiResponse(code = 404, message = "Atribuição de responsável não encontrada")
    })
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody ResponsavelTarefaDTO dto) {
        try {
            ResponsavelTarefa entidade = converter(dto);
            ResponsavelTarefa atualizada = service.atualizar(id, entidade);
            return ResponseEntity.ok(ResponsavelTarefaDTO.create(atualizada));
        } catch (RegraNegocioException e) {
            if ("Atribuição de responsável não encontrada.".equals(e.getMessage())) {
                return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Remover responsável de tarefa")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Atribuição removida com sucesso"),
            @ApiResponse(code = 404, message = "Atribuição de responsável não encontrada")
    })
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        try {
            service.excluir(id);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            if ("Atribuição de responsável não encontrada.".equals(e.getMessage())) {
                return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private ResponsavelTarefa converter(ResponsavelTarefaDTO dto) {
        if (dto == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        ResponsavelTarefa entidade = modelMapper.map(dto, ResponsavelTarefa.class);
        entidade.setId(null);

        if (dto.getIdTarefa() != null) {
            Tarefa tarefa = new Tarefa();
            tarefa.setId(dto.getIdTarefa());
            entidade.setTarefa(tarefa);
        } else {
            entidade.setTarefa(null);
        }

        if (dto.getIdFuncionarioProjeto() != null) {
            FuncionarioProjeto fp = new FuncionarioProjeto();
            fp.setId(dto.getIdFuncionarioProjeto());
            entidade.setFuncionarioProjeto(fp);
        } else {
            entidade.setFuncionarioProjeto(null);
        }

        return entidade;
    }
}