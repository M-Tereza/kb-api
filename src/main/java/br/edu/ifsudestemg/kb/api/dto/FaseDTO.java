package br.edu.ifsudestemg.kb.api.dto;

import br.edu.ifsudestemg.kb.model.entity.Fase;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaseDTO {

    @ApiModelProperty(value = "Identificador da fase", example = "0", position = 1)
    private Long id;

    @ApiModelProperty(value = "Nome da fase (até 60 caracteres)", example = "Em Revisão", required = true, position = 2)
    private String nome;

    @ApiModelProperty(value = "Ordem da fase no projeto (>= 1)", example = "6", required = true, position = 3)
    private Integer ordem;

    @ApiModelProperty(value = "Indica se a fase representa estado final", example = "false", required = true, position = 4)
    private Boolean ehFaseFinal;

    @ApiModelProperty(value = "ID do projeto vinculado", example = "0", required = true, position = 5)
    private Long idProjeto;

    @ApiModelProperty(value = "Título do projeto vinculado (retornado na consulta)", example = "Sistema Kanban", readOnly = true, position = 6)
    private String tituloProjeto;

    public static FaseDTO create(Fase entity) {
        if (entity == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        FaseDTO dto = modelMapper.map(entity, FaseDTO.class);
        if (entity.getProjeto() != null) {
            dto.setIdProjeto(entity.getProjeto().getId());
            dto.setTituloProjeto(entity.getProjeto().getTitulo());
        }
        return dto;
    }
}