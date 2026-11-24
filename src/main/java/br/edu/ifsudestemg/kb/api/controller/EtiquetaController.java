package br.edu.ifsudestemg.kb.api.controller;

import br.edu.ifsudestemg.kb.api.dto.EtiquetaDTO;
import br.edu.ifsudestemg.kb.exception.RegraNegocioException;
import br.edu.ifsudestemg.kb.model.entity.Etiqueta;
import br.edu.ifsudestemg.kb.model.entity.Projeto;
import br.edu.ifsudestemg.kb.service.EtiquetaService;
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
@RequestMapping("/api/v1/etiquetas")
@RequiredArgsConstructor
@CrossOrigin
public class EtiquetaController {

    private final EtiquetaService service;

    @GetMapping("/projeto/{projetoId}")
    @ApiOperation("Listar etiquetas de um projeto")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Etiquetas listadas com sucesso")
    })
    public ResponseEntity listarPorProjeto(@PathVariable("projetoId") Long projetoId) {
        List<EtiquetaDTO> dtos = service.listarPorProjeto(projetoId)
                .stream()
                .map(EtiquetaDTO::create)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @ApiOperation("Buscar etiqueta por ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Etiqueta encontrada"),
            @ApiResponse(code = 404, message = "Etiqueta não encontrada")
    })
    public ResponseEntity get(@PathVariable("id") Long id) {
        try {
            Etiqueta etiqueta = service.buscarPorId(id);
            return ResponseEntity.ok(EtiquetaDTO.create(etiqueta));
        } catch (RegraNegocioException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @ApiOperation("Cadastrar etiqueta")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Etiqueta cadastrada com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação ou regra de negócio")
    })
    public ResponseEntity post(@RequestBody EtiquetaDTO dto) {
        try {
            Etiqueta entidade = converter(dto);
            Etiqueta salva = service.salvar(entidade);
            return new ResponseEntity(EtiquetaDTO.create(salva), HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ApiOperation("Atualizar etiqueta")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Etiqueta atualizada com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação ou regra de negócio"),
            @ApiResponse(code = 404, message = "Etiqueta não encontrada")
    })
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody EtiquetaDTO dto) {
        try {
            Etiqueta entidade = converter(dto);
            Etiqueta atualizada = service.atualizar(id, entidade);
            return ResponseEntity.ok(EtiquetaDTO.create(atualizada));
        } catch (RegraNegocioException e) {
            if ("Etiqueta não encontrada.".equals(e.getMessage())) {
                return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Excluir etiqueta")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Etiqueta excluída com sucesso"),
            @ApiResponse(code = 404, message = "Etiqueta não encontrada")
    })
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        try {
            service.excluir(id);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            if ("Etiqueta não encontrada.".equals(e.getMessage())) {
                return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private Etiqueta converter(EtiquetaDTO dto) {
        if (dto == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        Etiqueta entidade = modelMapper.map(dto, Etiqueta.class);
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