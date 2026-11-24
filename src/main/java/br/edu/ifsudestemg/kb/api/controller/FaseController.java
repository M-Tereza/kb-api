package br.edu.ifsudestemg.kb.api.controller;

import br.edu.ifsudestemg.kb.api.dto.FaseDTO;
import br.edu.ifsudestemg.kb.exception.RegraNegocioException;
import br.edu.ifsudestemg.kb.model.entity.Fase;
import br.edu.ifsudestemg.kb.model.entity.Projeto;
import br.edu.ifsudestemg.kb.service.FaseService;
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
@RequestMapping("/api/v1/fases")
@RequiredArgsConstructor
@CrossOrigin
public class FaseController {

    private final FaseService service;

    @GetMapping("/projeto/{projetoId}")
    @ApiOperation("Listar fases de um projeto ordenadas")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Fases listadas com sucesso")
    })
    public ResponseEntity listarPorProjeto(@PathVariable("projetoId") Long projetoId) {
        List<FaseDTO> dtos = service.listarPorProjeto(projetoId)
                .stream()
                .map(FaseDTO::create)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @ApiOperation("Buscar fase por ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Fase encontrada"),
            @ApiResponse(code = 404, message = "Fase não encontrada")
    })
    public ResponseEntity get(@PathVariable("id") Long id) {
        try {
            Fase fase = service.buscarPorId(id);
            return ResponseEntity.ok(FaseDTO.create(fase));
        } catch (RegraNegocioException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @ApiOperation("Cadastrar fase")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Fase cadastrada com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação ou regra de negócio")
    })
    public ResponseEntity post(@RequestBody FaseDTO dto) {
        try {
            Fase entidade = converter(dto);
            Fase salva = service.salvar(entidade);
            return new ResponseEntity(FaseDTO.create(salva), HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ApiOperation("Atualizar fase")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Fase atualizada com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação ou regra de negócio"),
            @ApiResponse(code = 404, message = "Fase não encontrada")
    })
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody FaseDTO dto) {
        try {
            Fase entidade = converter(dto);
            Fase atualizada = service.atualizar(id, entidade);
            return ResponseEntity.ok(FaseDTO.create(atualizada));
        } catch (RegraNegocioException e) {
            if ("Fase não encontrada.".equals(e.getMessage())) {
                return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/mover-esquerda")
    @ApiOperation("Mover fase para a esquerda no quadro")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Fase movida para a esquerda"),
            @ApiResponse(code = 400, message = "Movimento inválido ou sem fase adjacente"),
            @ApiResponse(code = 404, message = "Fase não encontrada")
    })
    public ResponseEntity moverEsquerda(@PathVariable("id") Long id) {
        try {
            Fase movida = service.moverEsquerda(id);
            return ResponseEntity.ok(FaseDTO.create(movida));
        } catch (RegraNegocioException e) {
            if ("Fase não encontrada.".equals(e.getMessage())) {
                return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/mover-direita")
    @ApiOperation("Mover fase para a direita no quadro")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Fase movida para a direita"),
            @ApiResponse(code = 400, message = "Movimento inválido ou sem fase adjacente"),
            @ApiResponse(code = 404, message = "Fase não encontrada")
    })
    public ResponseEntity moverDireita(@PathVariable("id") Long id) {
        try {
            Fase movida = service.moverDireita(id);
            return ResponseEntity.ok(FaseDTO.create(movida));
        } catch (RegraNegocioException e) {
            if ("Fase não encontrada.".equals(e.getMessage())) {
                return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Excluir fase")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Fase excluída com sucesso"),
            @ApiResponse(code = 400, message = "Fase possui tarefas e exige confirmação"),
            @ApiResponse(code = 404, message = "Fase não encontrada")
    })
    public ResponseEntity excluir(
            @PathVariable("id") Long id,
            @RequestParam(name = "confirmarExclusaoTarefas", defaultValue = "false") boolean confirmarExclusaoTarefas
    ) {
        try {
            service.excluir(id, confirmarExclusaoTarefas);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            if ("Fase não encontrada.".equals(e.getMessage())) {
                return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private Fase converter(FaseDTO dto) {
        if (dto == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        Fase entidade = modelMapper.map(dto, Fase.class);
        entidade.setId(null);
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