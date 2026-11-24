package br.edu.ifsudestemg.kb.api.controller;

import br.edu.ifsudestemg.kb.api.dto.CargoDTO;
import br.edu.ifsudestemg.kb.exception.RegraNegocioException;
import br.edu.ifsudestemg.kb.model.entity.Cargo;
import br.edu.ifsudestemg.kb.service.CargoService;
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
@RequestMapping("/api/v1/cargos")
@RequiredArgsConstructor
@CrossOrigin
public class CargoController {

    private final CargoService service;

    @GetMapping
    @ApiOperation("Listar cargos")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Cargos listados com sucesso")
    })
    public ResponseEntity get() {
        List<CargoDTO> dtos = service.listarTodos()
                .stream()
                .map(CargoDTO::create)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @ApiOperation("Buscar cargo por ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Cargo encontrado"),
            @ApiResponse(code = 404, message = "Cargo não encontrado")
    })
    public ResponseEntity get(@PathVariable("id") Long id) {
        try {
            Cargo cargo = service.buscarPorId(id);
            return ResponseEntity.ok(CargoDTO.create(cargo));
        } catch (RegraNegocioException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @ApiOperation("Cadastrar cargo")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Cargo cadastrado com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação ou regra de negócio")
    })
    public ResponseEntity post(@RequestBody CargoDTO dto) {
        try {
            Cargo entidade = converter(dto);
            Cargo salvo = service.salvar(entidade);
            return new ResponseEntity(CargoDTO.create(salvo), HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ApiOperation("Atualizar cargo")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Cargo atualizado com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação ou regra de negócio"),
            @ApiResponse(code = 404, message = "Cargo não encontrado")
    })
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody CargoDTO dto) {
        try {
            Cargo entidade = converter(dto);
            Cargo atualizado = service.atualizar(id, entidade);
            return ResponseEntity.ok(CargoDTO.create(atualizado));
        } catch (RegraNegocioException e) {
            if ("Cargo não encontrado.".equals(e.getMessage())) {
                return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Excluir cargo")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Cargo excluído com sucesso"),
            @ApiResponse(code = 400, message = "Cargo vinculado a funcionários"),
            @ApiResponse(code = 404, message = "Cargo não encontrado")
    })
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        try {
            service.excluir(id);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            if ("Cargo não encontrado.".equals(e.getMessage())) {
                return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private Cargo converter(CargoDTO dto) {
        if (dto == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Cargo.class);
    }
}