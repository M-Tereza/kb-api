package br.edu.ifsudestemg.kb.api.dto;

import br.edu.ifsudestemg.kb.model.entity.FuncionarioProjeto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FuncionarioProjetoDTO {

    @ApiModelProperty(value = "Identificador do vínculo", example = "0", position = 1)
    private Long id;

    @ApiModelProperty(value = "ID do projeto vinculado", example = "0", required = true, position = 2)
    private Long idProjeto;

    @ApiModelProperty(value = "Título do projeto vinculado (retornado na consulta)", example = "Sistema Kanban", readOnly = true, position = 3)
    private String tituloProjeto;

    @ApiModelProperty(value = "ID do funcionário vinculado", example = "0", required = true, position = 4)
    private Long idFuncionario;

    @ApiModelProperty(value = "Nome do funcionário vinculado (retornado na consulta)", example = "Maria Silva", readOnly = true, position = 5)
    private String nomeFuncionario;

    @ApiModelProperty(value = "Indica se o funcionário é responsável pelo projeto", example = "true", required = true, position = 6)
    private Boolean ehResponsavel;

    public static FuncionarioProjetoDTO create(FuncionarioProjeto entity) {
        if (entity == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        FuncionarioProjetoDTO dto = modelMapper.map(entity, FuncionarioProjetoDTO.class);
        if (entity.getProjeto() != null) {
            dto.setIdProjeto(entity.getProjeto().getId());
            dto.setTituloProjeto(entity.getProjeto().getTitulo());
        }
        if (entity.getFuncionario() != null) {
            dto.setIdFuncionario(entity.getFuncionario().getId());
            dto.setNomeFuncionario(entity.getFuncionario().getNome());
        }
        return dto;
    }
}