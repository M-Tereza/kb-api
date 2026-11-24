package br.edu.ifsudestemg.kb.api.controller;

import br.edu.ifsudestemg.kb.api.dto.AreaProjetoDTO;
import br.edu.ifsudestemg.kb.exception.RegraNegocioException;
import br.edu.ifsudestemg.kb.model.entity.AreaProjeto;
import br.edu.ifsudestemg.kb.service.AreaProjetoService;
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
@RequestMapping("/api/v1/areas-projeto")
@RequiredArgsConstructor
@CrossOrigin
public class AreaProjetoController {

    private final AreaProjetoService service;

    @GetMapping
    @ApiOperation("Listar áreas de projeto")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Áreas de projeto listadas com sucesso")
    })
    public ResponseEntity get() {
        List<AreaProjetoDTO> dtos = service.listarTodas()
                .stream()
                .map(AreaProjetoDTO::create)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @ApiOperation("Buscar área de projeto por ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Área de projeto encontrada"),
            @ApiResponse(code = 404, message = "Área de projeto não encontrada")
    })
    public ResponseEntity get(@PathVariable("id") Long id) {
        try {
            AreaProjeto area = service.buscarPorId(id);
            return ResponseEntity.ok(AreaProjetoDTO.create(area));
        } catch (RegraNegocioException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @ApiOperation("Cadastrar área de projeto")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Área de projeto cadastrada com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação ou regra de negócio")
    })
    public ResponseEntity post(@RequestBody AreaProjetoDTO dto) {
        try {
            AreaProjeto entidade = converter(dto);
            AreaProjeto salva = service.salvar(entidade);
            return new ResponseEntity(AreaProjetoDTO.create(salva), HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ApiOperation("Atualizar área de projeto")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Área de projeto atualizada com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação ou regra de negócio"),
            @ApiResponse(code = 404, message = "Área de projeto não encontrada")
    })
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody AreaProjetoDTO dto) {
        try {
            AreaProjeto entidade = converter(dto);
            AreaProjeto atualizada = service.atualizar(id, entidade);
            return ResponseEntity.ok(AreaProjetoDTO.create(atualizada));
        } catch (RegraNegocioException e) {
            if ("Área de projeto não encontrada.".equals(e.getMessage())) {
                return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Excluir área de projeto")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Área de projeto excluída com sucesso"),
            @ApiResponse(code = 400, message = "Área de projeto vinculada a projetos"),
            @ApiResponse(code = 404, message = "Área de projeto não encontrada")
    })
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        try {
            service.excluir(id);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            if ("Área de projeto não encontrada.".equals(e.getMessage())) {
                return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private AreaProjeto converter(AreaProjetoDTO dto) {
        if (dto == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, AreaProjeto.class);
    }
}