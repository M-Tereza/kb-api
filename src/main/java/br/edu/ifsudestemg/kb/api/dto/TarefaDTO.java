package br.edu.ifsudestemg.kb.api.dto;

import br.edu.ifsudestemg.kb.model.entity.Tarefa;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarefaDTO {

    @ApiModelProperty(value = "Identificador da tarefa", example = "0", position = 1)
    private Long id;

    @ApiModelProperty(value = "Título da tarefa (até 120 caracteres)", example = "Implementar autenticação e rotas", required = true, position = 2)
    private String titulo;

    @ApiModelProperty(value = "Descrição da tarefa (até 500 caracteres)", example = "Configurar endpoints e validar regras", position = 3)
    private String descricao;

    @ApiModelProperty(value = "Data/hora de criação (ISO-8601 UTC)", example = "2026-11-24T12:00:00Z", position = 4)
    private Instant dataCriacao;

    @ApiModelProperty(value = "Data/hora limite (ISO-8601 UTC)", example = "2026-12-01T18:00:00Z", position = 5)
    private Instant dataLimite;

    @ApiModelProperty(value = "ID do projeto vinculado", example = "0", required = true, position = 6)
    private Long idProjeto;

    @ApiModelProperty(value = "Título do projeto vinculado (retornado na consulta)", example = "Sistema Kanban", readOnly = true, position = 7)
    private String tituloProjeto;

    @ApiModelProperty(value = "ID da fase vinculada", example = "0", required = true, position = 8)
    private Long idFase;

    @ApiModelProperty(value = "Nome da fase vinculada (retornado na consulta)", example = "Backlog", readOnly = true, position = 9)
    private String nomeFase;

    @ApiModelProperty(value = "ID da prioridade vinculada", example = "0", required = true, position = 10)
    private Long idPrioridade;

    @ApiModelProperty(value = "Nome da prioridade vinculada (retornado na consulta)", example = "Alta", readOnly = true, position = 11)
    private String nomePrioridade;

    @ApiModelProperty(value = "Cor da prioridade vinculada (retornado na consulta)", example = "#3B82F6", readOnly = true, position = 12)
    private String corPrioridade;

    public static TarefaDTO create(Tarefa entity) {
        if (entity == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        TarefaDTO dto = modelMapper.map(entity, TarefaDTO.class);
        if (entity.getFase() != null) {
            dto.setIdFase(entity.getFase().getId());
            dto.setNomeFase(entity.getFase().getNome());
            if (entity.getFase().getProjeto() != null) {
                dto.setIdProjeto(entity.getFase().getProjeto().getId());
                dto.setTituloProjeto(entity.getFase().getProjeto().getTitulo());
            }
        }
        if (entity.getPrioridade() != null) {
            dto.setIdPrioridade(entity.getPrioridade().getId());
            dto.setNomePrioridade(entity.getPrioridade().getNome());
            dto.setCorPrioridade(entity.getPrioridade().getCor());
        }
        return dto;
    }
}