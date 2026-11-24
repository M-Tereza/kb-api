package br.edu.ifsudestemg.kb.api.dto;

import br.edu.ifsudestemg.kb.model.entity.TarefaEtiqueta;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarefaEtiquetaDTO {

    @ApiModelProperty(value = "Identificador do vínculo", example = "0", position = 1)
    private Long id;

    @ApiModelProperty(value = "ID da tarefa", example = "0", required = true, position = 2)
    private Long idTarefa;

    @ApiModelProperty(value = "Título da tarefa (retornado na consulta)", example = "Criar endpoints REST", readOnly = true, position = 3)
    private String tituloTarefa;

    @ApiModelProperty(value = "ID da etiqueta", example = "0", required = true, position = 4)
    private Long idEtiqueta;

    @ApiModelProperty(value = "Nome da etiqueta (retornado na consulta)", example = "Urgente", readOnly = true, position = 5)
    private String nomeEtiqueta;

    public static TarefaEtiquetaDTO create(TarefaEtiqueta entity) {
        if (entity == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        TarefaEtiquetaDTO dto = modelMapper.map(entity, TarefaEtiquetaDTO.class);
        if (entity.getTarefa() != null) {
            dto.setIdTarefa(entity.getTarefa().getId());
            dto.setTituloTarefa(entity.getTarefa().getTitulo());
        }
        if (entity.getEtiqueta() != null) {
            dto.setIdEtiqueta(entity.getEtiqueta().getId());
            dto.setNomeEtiqueta(entity.getEtiqueta().getNome());
        }
        return dto;
    }
}