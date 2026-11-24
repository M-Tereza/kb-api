package br.edu.ifsudestemg.kb.api.controller;

import br.edu.ifsudestemg.kb.api.dto.AnotacaoDTO;
import br.edu.ifsudestemg.kb.exception.RegraNegocioException;
import br.edu.ifsudestemg.kb.model.entity.Anotacao;
import br.edu.ifsudestemg.kb.model.entity.Tarefa;
import br.edu.ifsudestemg.kb.service.AnotacaoService;
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
@RequestMapping("/api/v1/anotacoes")
@RequiredArgsConstructor
@CrossOrigin
public class AnotacaoController {

    private final AnotacaoService service;

    @GetMapping("/tarefa/{tarefaId}")
    @ApiOperation("Listar anotações de uma tarefa")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Anotações listadas com sucesso")
    })
    public ResponseEntity listarPorTarefa(@PathVariable("tarefaId") Long tarefaId) {
        List<AnotacaoDTO> dtos = service.listarPorTarefa(tarefaId)
                .stream()
                .map(AnotacaoDTO::create)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @ApiOperation("Buscar anotação por ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Anotação encontrada"),
            @ApiResponse(code = 404, message = "Anotação não encontrada")
    })
    public ResponseEntity get(@PathVariable("id") Long id) {
        try {
            Anotacao anotacao = service.buscarPorId(id);
            return ResponseEntity.ok(AnotacaoDTO.create(anotacao));
        } catch (RegraNegocioException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @ApiOperation("Cadastrar anotação")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Anotação cadastrada com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação ou regra de negócio")
    })
    public ResponseEntity post(@RequestBody AnotacaoDTO dto) {
        try {
            Anotacao entidade = converter(dto);
            Anotacao salva = service.salvar(entidade);
            return new ResponseEntity(AnotacaoDTO.create(salva), HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ApiOperation("Atualizar anotação")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Anotação atualizada com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação ou regra de negócio"),
            @ApiResponse(code = 404, message = "Anotação não encontrada")
    })
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody AnotacaoDTO dto) {
        try {
            Anotacao entidade = converter(dto);
            Anotacao atualizada = service.atualizar(id, entidade);
            return ResponseEntity.ok(AnotacaoDTO.create(atualizada));
        } catch (RegraNegocioException e) {
            if ("Anotação não encontrada.".equals(e.getMessage())) {
                return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Excluir anotação")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Anotação excluída com sucesso"),
            @ApiResponse(code = 404, message = "Anotação não encontrada")
    })
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        try {
            service.excluir(id);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            if ("Anotação não encontrada.".equals(e.getMessage())) {
                return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private Anotacao converter(AnotacaoDTO dto) {
        if (dto == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        Anotacao entidade = modelMapper.map(dto, Anotacao.class);
        entidade.setId(null);
        if (dto.getIdTarefa() != null) {
            Tarefa tarefa = new Tarefa();
            tarefa.setId(dto.getIdTarefa());
            entidade.setTarefa(tarefa);
        } else {
            entidade.setTarefa(null);
        }
        return entidade;
    }
}