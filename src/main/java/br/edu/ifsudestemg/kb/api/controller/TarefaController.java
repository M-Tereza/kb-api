package br.edu.ifsudestemg.kb.api.controller;

import br.edu.ifsudestemg.kb.api.dto.TarefaDTO;
import br.edu.ifsudestemg.kb.exception.RegraNegocioException;
import br.edu.ifsudestemg.kb.model.entity.Fase;
import br.edu.ifsudestemg.kb.model.entity.Prioridade;
import br.edu.ifsudestemg.kb.model.entity.Tarefa;
import br.edu.ifsudestemg.kb.service.TarefaService;
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
@RequestMapping("/api/v1/tarefas")
@RequiredArgsConstructor
@CrossOrigin
public class TarefaController {

    private final TarefaService service;

    @GetMapping
    @ApiOperation("Listar tarefas")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Tarefas listadas com sucesso")
    })
    public ResponseEntity get() {
        List<TarefaDTO> dtos = service.listarTodos()
                .stream()
                .map(TarefaDTO::create)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/projeto/{projetoId}")
    @ApiOperation("Listar tarefas de um projeto")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Tarefas do projeto listadas com sucesso")
    })
    public ResponseEntity listarPorProjeto(@PathVariable("projetoId") Long projetoId) {
        List<TarefaDTO> dtos = service.listarPorProjeto(projetoId)
                .stream()
                .map(TarefaDTO::create)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/fase/{faseId}")
    @ApiOperation("Listar tarefas de uma fase")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Tarefas da fase listadas com sucesso")
    })
    public ResponseEntity listarPorFase(@PathVariable("faseId") Long faseId) {
        List<TarefaDTO> dtos = service.listarPorFase(faseId)
                .stream()
                .map(TarefaDTO::create)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @ApiOperation("Buscar tarefa por ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Tarefa encontrada"),
            @ApiResponse(code = 404, message = "Tarefa não encontrada")
    })
    public ResponseEntity get(@PathVariable("id") Long id) {
        try {
            Tarefa tarefa = service.buscarPorId(id);
            return ResponseEntity.ok(TarefaDTO.create(tarefa));
        } catch (RegraNegocioException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @ApiOperation("Cadastrar tarefa")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Tarefa cadastrada com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação ou regra de negócio")
    })
    public ResponseEntity post(@RequestBody TarefaDTO dto) {
        try {
            Tarefa entidade = converter(dto);
            Tarefa salva = service.salvar(entidade);
            return new ResponseEntity(TarefaDTO.create(salva), HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ApiOperation("Atualizar tarefa")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Tarefa atualizada com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação ou regra de negócio"),
            @ApiResponse(code = 404, message = "Tarefa não encontrada")
    })
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody TarefaDTO dto) {
        try {
            Tarefa entidade = converter(dto);
            Tarefa atualizada = service.atualizar(id, entidade);
            return ResponseEntity.ok(TarefaDTO.create(atualizada));
        } catch (RegraNegocioException e) {
            if ("Tarefa não encontrada.".equals(e.getMessage())) {
                return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Excluir tarefa")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Tarefa excluída com sucesso"),
            @ApiResponse(code = 404, message = "Tarefa não encontrada")
    })
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        try {
            service.excluir(id);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            if ("Tarefa não encontrada.".equals(e.getMessage())) {
                return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private Tarefa converter(TarefaDTO dto) {
        if (dto == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        Tarefa entidade = modelMapper.map(dto, Tarefa.class);
        entidade.setId(null);

        if (dto.getIdFase() != null) {
            Fase fase = new Fase();
            fase.setId(dto.getIdFase());
            entidade.setFase(fase);
        } else {
            entidade.setFase(null);
        }

        if (dto.getIdPrioridade() != null) {
            Prioridade prioridade = new Prioridade();
            prioridade.setId(dto.getIdPrioridade());
            entidade.setPrioridade(prioridade);
        } else {
            entidade.setPrioridade(null);
        }

        return entidade;
    }
}