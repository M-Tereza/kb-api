package br.edu.ifsudestemg.kb.api.dto;

import br.edu.ifsudestemg.kb.model.entity.Etiqueta;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EtiquetaDTO {

    @ApiModelProperty(value = "Identificador da etiqueta", example = "0", position = 1)
    private Long id;

    @ApiModelProperty(value = "Nome da etiqueta", example = "Urgente", required = true, position = 2)
    private String nome;

    @ApiModelProperty(value = "Cor da etiqueta (hexadecimal ou nome)", example = "#FF5733", position = 3)
    private String cor;

    @ApiModelProperty(value = "ID do projeto vinculado", example = "0", required = true, position = 4)
    private Long idProjeto;

    @ApiModelProperty(value = "Título do projeto vinculado (retornado na consulta)", example = "Sistema Kanban", readOnly = true, position = 5)
    private String tituloProjeto;

    public static EtiquetaDTO create(Etiqueta entity) {
        if (entity == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        EtiquetaDTO dto = modelMapper.map(entity, EtiquetaDTO.class);
        if (entity.getProjeto() != null) {
            dto.setIdProjeto(entity.getProjeto().getId());
            dto.setTituloProjeto(entity.getProjeto().getTitulo());
        }
        return dto;
    }
}