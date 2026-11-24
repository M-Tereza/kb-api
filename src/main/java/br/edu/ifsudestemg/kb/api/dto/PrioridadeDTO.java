package br.edu.ifsudestemg.kb.api.dto;

import br.edu.ifsudestemg.kb.model.entity.Prioridade;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrioridadeDTO {

    @ApiModelProperty(value = "Identificador da prioridade", example = "0", position = 1)
    private Long id;

    @ApiModelProperty(value = "Nome da prioridade", example = "Alta", required = true, position = 2)
    private String nome;

    public static PrioridadeDTO create(Prioridade entity) {
        if (entity == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(entity, PrioridadeDTO.class);
    }
}