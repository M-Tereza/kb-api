package br.edu.ifsudestemg.kb.api.dto;

import br.edu.ifsudestemg.kb.model.entity.AreaProjeto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AreaProjetoDTO {

    @ApiModelProperty(value = "Identificador da área de projeto", example = "0", position = 1)
    private Long id;

    @ApiModelProperty(value = "Nome da área de projeto", example = "Desenvolvimento de Sistemas", required = true, position = 2)
    private String nome;

    public static AreaProjetoDTO create(AreaProjeto entity) {
        if (entity == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(entity, AreaProjetoDTO.class);
    }
}