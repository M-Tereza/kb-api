package br.edu.ifsudestemg.kb.api.dto;

import br.edu.ifsudestemg.kb.model.entity.ResponsavelTarefa;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponsavelTarefaDTO {

    @ApiModelProperty(value = "Identificador da atribuição", example = "0", position = 1)
    private Long id;

    @ApiModelProperty(value = "ID da tarefa", example = "0", required = true, position = 2)
    private Long idTarefa;

    @ApiModelProperty(value = "Título da tarefa (retornado na consulta)", example = "Criar endpoints REST", readOnly = true, position = 3)
    private String tituloTarefa;

    @ApiModelProperty(value = "ID do vínculo funcionário-projeto", example = "0", required = true, position = 4)
    private Long idFuncionarioProjeto;

    @ApiModelProperty(value = "ID do funcionário (retornado na consulta)", example = "0", readOnly = true, position = 5)
    private Long idFuncionario;

    @ApiModelProperty(value = "Nome do funcionário (retornado na consulta)", example = "Maria Silva", readOnly = true, position = 6)
    private String nomeFuncionario;

    public static ResponsavelTarefaDTO create(ResponsavelTarefa entity) {
        if (entity == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        ResponsavelTarefaDTO dto = modelMapper.map(entity, ResponsavelTarefaDTO.class);
        if (entity.getTarefa() != null) {
            dto.setIdTarefa(entity.getTarefa().getId());
            dto.setTituloTarefa(entity.getTarefa().getTitulo());
        }
        if (entity.getFuncionarioProjeto() != null) {
            dto.setIdFuncionarioProjeto(entity.getFuncionarioProjeto().getId());
            if (entity.getFuncionarioProjeto().getFuncionario() != null) {
                dto.setIdFuncionario(entity.getFuncionarioProjeto().getFuncionario().getId());
                dto.setNomeFuncionario(entity.getFuncionarioProjeto().getFuncionario().getNome());
            }
        }
        return dto;
    }
}