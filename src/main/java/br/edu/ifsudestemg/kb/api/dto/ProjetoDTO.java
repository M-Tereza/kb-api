package br.edu.ifsudestemg.kb.api.dto;

import br.edu.ifsudestemg.kb.model.entity.Projeto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjetoDTO {

    @ApiModelProperty(value = "Identificador do projeto", example = "0", position = 1)
    private Long id;

    @ApiModelProperty(value = "Título do projeto", example = "Portal Acadêmico", required = true, position = 2)
    private String titulo;

    @ApiModelProperty(value = "Descrição do projeto (até 500 caracteres)", example = "Gestão de demandas e sprints da equipe", position = 3)
    private String descricao;

    @ApiModelProperty(value = "ID da área de projeto (opcional)", example = "0", position = 4)
    private Long idAreaProjeto;

    @ApiModelProperty(value = "Nome da área de projeto (retornado na consulta)", example = "Desenvolvimento de Sistemas", readOnly = true, position = 5)
    private String nomeAreaProjeto;

    public static ProjetoDTO create(Projeto entity) {
        if (entity == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        ProjetoDTO dto = modelMapper.map(entity, ProjetoDTO.class);
        if (entity.getAreaProjeto() != null) {
            dto.setIdAreaProjeto(entity.getAreaProjeto().getId());
            dto.setNomeAreaProjeto(entity.getAreaProjeto().getNome());
        }
        return dto;
    }
}