package br.edu.ifsudestemg.kb.api.controller;

import br.edu.ifsudestemg.kb.api.dto.PrioridadeDTO;
import br.edu.ifsudestemg.kb.exception.RegraNegocioException;
import br.edu.ifsudestemg.kb.model.entity.Prioridade;
import br.edu.ifsudestemg.kb.service.PrioridadeService;
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
@RequestMapping("/api/v1/prioridades")
@RequiredArgsConstructor
@CrossOrigin
public class PrioridadeController {

    private final PrioridadeService service;

    @GetMapping
    @ApiOperation("Listar prioridades")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Prioridades listadas com sucesso")
    })
    public ResponseEntity get() {
        List<PrioridadeDTO> dtos = service.listarTodas()
                .stream()
                .map(PrioridadeDTO::create)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @ApiOperation("Buscar prioridade por ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Prioridade encontrada"),
            @ApiResponse(code = 404, message = "Prioridade não encontrada")
    })
    public ResponseEntity get(@PathVariable("id") Long id) {
        try {
            Prioridade prioridade = service.buscarPorId(id);
            return ResponseEntity.ok(PrioridadeDTO.create(prioridade));
        } catch (RegraNegocioException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @ApiOperation("Cadastrar prioridade")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Prioridade cadastrada com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação ou regra de negócio")
    })
    public ResponseEntity post(@RequestBody PrioridadeDTO dto) {
        try {
            Prioridade entidade = converter(dto);
            Prioridade salva = service.salvar(entidade);
            return new ResponseEntity(PrioridadeDTO.create(salva), HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ApiOperation("Atualizar prioridade")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Prioridade atualizada com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação ou regra de negócio"),
            @ApiResponse(code = 404, message = "Prioridade não encontrada")
    })
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody PrioridadeDTO dto) {
        try {
            Prioridade entidade = converter(dto);
            Prioridade atualizada = service.atualizar(id, entidade);
            return ResponseEntity.ok(PrioridadeDTO.create(atualizada));
        } catch (RegraNegocioException e) {
            if ("Prioridade não encontrada.".equals(e.getMessage())) {
                return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Excluir prioridade")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Prioridade excluída com sucesso"),
            @ApiResponse(code = 404, message = "Prioridade não encontrada")
    })
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        try {
            service.excluir(id);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            if ("Prioridade não encontrada.".equals(e.getMessage())) {
                return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private Prioridade converter(PrioridadeDTO dto) {
        if (dto == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Prioridade.class);
    }
}