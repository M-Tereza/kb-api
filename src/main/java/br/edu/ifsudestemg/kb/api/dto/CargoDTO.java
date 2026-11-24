package br.edu.ifsudestemg.kb.api.dto;

import br.edu.ifsudestemg.kb.model.entity.Cargo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CargoDTO {

    @ApiModelProperty(value = "Identificador do cargo", example = "0", position = 1)
    private Long id;

    @ApiModelProperty(value = "Nome do cargo", example = "Desenvolvedor Backend", required = true, position = 2)
    private String nome;

    public static CargoDTO create(Cargo entity) {
        if (entity == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(entity, CargoDTO.class);
    }
}