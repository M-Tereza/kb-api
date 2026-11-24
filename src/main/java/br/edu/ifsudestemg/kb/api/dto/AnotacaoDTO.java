package br.edu.ifsudestemg.kb.api.dto;

import br.edu.ifsudestemg.kb.model.entity.Anotacao;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnotacaoDTO {

    @ApiModelProperty(value = "Identificador da anotação", example = "0", position = 1)
    private Long id;

    @ApiModelProperty(value = "Texto da anotação", example = "Revisar endpoint de tarefas antes do teste", required = true, position = 2)
    private String texto;

    @ApiModelProperty(value = "Data/hora de criação (ISO-8601 UTC)", example = "2026-11-24T12:00:00Z", position = 3)
    private Instant dataCriacao;

    @ApiModelProperty(value = "ID da tarefa vinculada", example = "0", required = true, position = 4)
    private Long idTarefa;

    @ApiModelProperty(value = "Título da tarefa vinculada (retornado na consulta)", example = "Implementar CRUD de fases", readOnly = true, position = 5)
    private String tituloTarefa;

    public static AnotacaoDTO create(Anotacao entity) {
        if (entity == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        AnotacaoDTO dto = modelMapper.map(entity, AnotacaoDTO.class);
        if (entity.getTarefa() != null) {
            dto.setIdTarefa(entity.getTarefa().getId());
            dto.setTituloTarefa(entity.getTarefa().getTitulo());
        }
        return dto;
    }
}