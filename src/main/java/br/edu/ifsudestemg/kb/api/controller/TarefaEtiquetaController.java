package br.edu.ifsudestemg.kb.api.controller;

import br.edu.ifsudestemg.kb.api.dto.TarefaEtiquetaDTO;
import br.edu.ifsudestemg.kb.exception.RegraNegocioException;
import br.edu.ifsudestemg.kb.model.entity.Etiqueta;
import br.edu.ifsudestemg.kb.model.entity.Tarefa;
import br.edu.ifsudestemg.kb.model.entity.TarefaEtiqueta;
import br.edu.ifsudestemg.kb.service.TarefaEtiquetaService;
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
@RequestMapping("/api/v1/tarefas-etiquetas")
@RequiredArgsConstructor
@CrossOrigin
public class TarefaEtiquetaController {

    private final TarefaEtiquetaService service;

    @GetMapping("/tarefa/{tarefaId}")
    @ApiOperation("Listar etiquetas vinculadas a uma tarefa")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Etiquetas da tarefa listadas com sucesso")
    })
    public ResponseEntity listarPorTarefa(@PathVariable("tarefaId") Long tarefaId) {
        List<TarefaEtiquetaDTO> dtos = service.listarPorTarefa(tarefaId)
                .stream()
                .map(TarefaEtiquetaDTO::create)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/etiqueta/{etiquetaId}")
    @ApiOperation("Listar tarefas vinculadas a uma etiqueta")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Tarefas da etiqueta listadas com sucesso")
    })
    public ResponseEntity listarPorEtiqueta(@PathVariable("etiquetaId") Long etiquetaId) {
        List<TarefaEtiquetaDTO> dtos = service.listarPorEtiqueta(etiquetaId)
                .stream()
                .map(TarefaEtiquetaDTO::create)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @ApiOperation("Buscar vínculo de etiqueta na tarefa por ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Vínculo encontrado"),
            @ApiResponse(code = 404, message = "Vínculo de etiqueta na tarefa não encontrado")
    })
    public ResponseEntity get(@PathVariable("id") Long id) {
        try {
            TarefaEtiqueta vinculo = service.buscarPorId(id);
            return ResponseEntity.ok(TarefaEtiquetaDTO.create(vinculo));
        } catch (RegraNegocioException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @ApiOperation("Vincular etiqueta a tarefa")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Etiqueta vinculada com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação ou projetos incompatíveis")
    })
    public ResponseEntity post(@RequestBody TarefaEtiquetaDTO dto) {
        try {
            TarefaEtiqueta entidade = converter(dto);
            TarefaEtiqueta salvo = service.salvar(entidade);
            return new ResponseEntity(TarefaEtiquetaDTO.create(salvo), HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ApiOperation("Atualizar vínculo de etiqueta na tarefa")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Vínculo atualizado com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação ou projetos incompatíveis"),
            @ApiResponse(code = 404, message = "Vínculo de etiqueta na tarefa não encontrado")
    })
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody TarefaEtiquetaDTO dto) {
        try {
            TarefaEtiqueta entidade = converter(dto);
            TarefaEtiqueta atualizada = service.atualizar(id, entidade);
            return ResponseEntity.ok(TarefaEtiquetaDTO.create(atualizada));
        } catch (RegraNegocioException e) {
            if ("Vínculo de etiqueta na tarefa não encontrado.".equals(e.getMessage())) {
                return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Desvincular etiqueta de tarefa")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Vínculo removido com sucesso"),
            @ApiResponse(code = 404, message = "Vínculo de etiqueta na tarefa não encontrado")
    })
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        try {
            service.excluir(id);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            if ("Vínculo de etiqueta na tarefa não encontrado.".equals(e.getMessage())) {
                return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private TarefaEtiqueta converter(TarefaEtiquetaDTO dto) {
        if (dto == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        TarefaEtiqueta entidade = modelMapper.map(dto, TarefaEtiqueta.class);
        entidade.setId(null);

        if (dto.getIdTarefa() != null) {
            Tarefa tarefa = new Tarefa();
            tarefa.setId(dto.getIdTarefa());
            entidade.setTarefa(tarefa);
        } else {
            entidade.setTarefa(null);
        }

        if (dto.getIdEtiqueta() != null) {
            Etiqueta etiqueta = new Etiqueta();
            etiqueta.setId(dto.getIdEtiqueta());
            entidade.setEtiqueta(etiqueta);
        } else {
            entidade.setEtiqueta(null);
        }

        return entidade;
    }
}