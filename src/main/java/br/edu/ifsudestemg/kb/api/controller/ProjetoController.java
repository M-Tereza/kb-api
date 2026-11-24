package br.edu.ifsudestemg.kb.api.controller;

import br.edu.ifsudestemg.kb.api.dto.ProjetoDTO;
import br.edu.ifsudestemg.kb.exception.RegraNegocioException;
import br.edu.ifsudestemg.kb.model.entity.AreaProjeto;
import br.edu.ifsudestemg.kb.model.entity.Projeto;
import br.edu.ifsudestemg.kb.service.ProjetoService;
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
@RequestMapping("/api/v1/projetos")
@RequiredArgsConstructor
@CrossOrigin
public class ProjetoController {

    private final ProjetoService service;

    @GetMapping
    @ApiOperation("Listar projetos")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Projetos listados com sucesso")
    })
    public ResponseEntity get() {
        List<ProjetoDTO> dtos = service.listarTodos()
                .stream()
                .map(ProjetoDTO::create)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @ApiOperation("Buscar projeto por ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Projeto encontrado"),
            @ApiResponse(code = 404, message = "Projeto não encontrado")
    })
    public ResponseEntity get(@PathVariable("id") Long id) {
        try {
            Projeto projeto = service.buscarPorId(id);
            return ResponseEntity.ok(ProjetoDTO.create(projeto));
        } catch (RegraNegocioException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @ApiOperation("Cadastrar projeto com fases padrão")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Projeto cadastrado com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação ou regra de negócio")
    })
    public ResponseEntity post(@RequestBody ProjetoDTO dto) {
        try {
            Projeto entidade = converter(dto);
            Projeto salvo = service.salvar(entidade);
            return new ResponseEntity(ProjetoDTO.create(salvo), HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ApiOperation("Atualizar projeto")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Projeto atualizado com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação ou regra de negócio"),
            @ApiResponse(code = 404, message = "Projeto não encontrado")
    })
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody ProjetoDTO dto) {
        try {
            Projeto entidade = converter(dto);
            Projeto atualizado = service.atualizar(id, entidade);
            return ResponseEntity.ok(ProjetoDTO.create(atualizado));
        } catch (RegraNegocioException e) {
            if ("Projeto não encontrado.".equals(e.getMessage())) {
                return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Excluir projeto e dados vinculados")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Projeto excluído com sucesso"),
            @ApiResponse(code = 404, message = "Projeto não encontrado")
    })
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        try {
            service.excluir(id);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            if ("Projeto não encontrado.".equals(e.getMessage())) {
                return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private Projeto converter(ProjetoDTO dto) {
        if (dto == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        Projeto entidade = modelMapper.map(dto, Projeto.class);
        entidade.setId(null);
        if (dto.getIdAreaProjeto() != null) {
            AreaProjeto area = new AreaProjeto();
            area.setId(dto.getIdAreaProjeto());
            entidade.setAreaProjeto(area);
        } else {
            entidade.setAreaProjeto(null);
        }
        return entidade;
    }
}