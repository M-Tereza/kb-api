package br.edu.ifsudestemg.kb.api.dto;

import br.edu.ifsudestemg.kb.model.entity.Funcionario;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FuncionarioDTO {

    @ApiModelProperty(value = "Identificador do funcionário", example = "0", position = 1)
    private Long id;

    @ApiModelProperty(value = "Nome completo do funcionário", example = "Maria Silva", required = true, position = 2)
    private String nome;

    @ApiModelProperty(value = "E-mail do funcionário", example = "maria.silva@exemplo.com", required = true, position = 3)
    private String email;

    @ApiModelProperty(value = "Data de nascimento (formato YYYY-MM-DD)", example = "2000-08-15", position = 4)
    private LocalDate dataNascimento;

    @ApiModelProperty(value = "Telefone de contato", example = "(32) 99999-0000", position = 5)
    private String telefone;

    @ApiModelProperty(value = "Biografia ou observação do funcionário", example = "Desenvolvedora full-stack no time Kanban", position = 6)
    private String bio;

    @ApiModelProperty(value = "ID do cargo vinculado (opcional)", example = "0", position = 7)
    private Long idCargo;

    @ApiModelProperty(value = "Nome do cargo vinculado (retornado na consulta)", example = "Desenvolvedor Backend", readOnly = true, position = 8)
    private String nomeCargo;

    public static FuncionarioDTO create(Funcionario entity) {
        if (entity == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        FuncionarioDTO dto = modelMapper.map(entity, FuncionarioDTO.class);
        if (entity.getCargo() != null) {
            dto.setIdCargo(entity.getCargo().getId());
            dto.setNomeCargo(entity.getCargo().getNome());
        }
        return dto;
    }
}