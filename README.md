# Sistema Kanban — Backend (`kb-api`)

API RESTful desenvolvida em **Java** e **Spring Boot** para o **Gerenciador de Projetos e Tarefas (Kanban)**. O serviço centraliza o domínio da aplicação, regras de integridade referencial, validações de negócio e persistência relacional no **PostgreSQL**, fornecendo endpoints JSON para consumo pelo frontend SPA.

## Visão Geral e Recursos

* **Gestão de Projetos e Fluxos Kanban**
  * **Projetos (`/api/v1/projetos`)**: CRUD completo com vinculação opcional a áreas de negócio e geração automática das 5 fases padrão (*Backlog*, *Fazendo*, *Pausado*, *Concluído* e *Arquivado*) na criação.
  * **Fases (`/api/v1/fases`)**: Gerenciamento das colunas do quadro por projeto, controle de estado final (`ehFaseFinal`), reordenação segura entre fases adjacentes (`/mover-esquerda` e `/mover-direita`) e exclusão em cascata protegida por confirmação (`confirmarExclusaoTarefas=true`) quando há tarefas alocadas.

* **Gestão de Tarefas e Colaboração**
  * **Tarefas (`/api/v1/tarefas`)**: Cadastro, consulta (geral, por projeto ou por fase), movimentação entre fases e validação de prazos (`dataLimite` não anterior à `dataCriacao` nem no passado).
  * **Responsáveis por Tarefa (`/api/v1/responsaveis-tarefa`)**: Atribuição de múltiplos colaboradores a uma tarefa, garantindo que os responsáveis sejam participantes ativos do respectivo projeto.
  * **Etiquetas por Tarefa (`/api/v1/tarefas-etiquetas`)**: Classificação de tarefas com etiquetas pertencentes ao mesmo projeto da tarefa.
  * **Anotações (`/api/v1/anotacoes`)**: Registro histórico de comentários e observações vinculadas diretamente às tarefas.

* **Equipe e Cadastros Auxiliares**
  * **Funcionários (`/api/v1/funcionarios`)** e **Cargos (`/api/v1/cargos`)**: Gestão de colaboradores, dados de perfil e bloqueio de exclusão de cargos em uso.
  * **Participações no Projeto (`/api/v1/funcionarios-projeto`)**: Associação entre colaboradores e projetos com definição de papel (`ehResponsavel`).
  * **Áreas de Projeto (`/api/v1/areas-projeto`)**: Categorização de projetos com proteção contra exclusão enquanto houver vínculos.
  * **Prioridades (`/api/v1/prioridades`)** e **Etiquetas (`/api/v1/etiquetas`)**: Classificações globais e por projeto com suporte a cores hexadecimais (`#RGB` ou `#RRGGBB`, aplicando `#3B82F6` por padrão).

## Stack Tecnológica

| Camada / Finalidade | Tecnologia / Biblioteca | Detalhes e Uso no Projeto |
| :--- | :--- | :--- |
| **Linguagem** | Java | `17` |
| **Framework Principal** | Spring Boot | `2.5.x` (`@RestController`, `@Service`, `@Transactional`, `@CrossOrigin`, `ResponseEntity`) |
| **Persistência & ORM** | Spring Data JPA / Hibernate | `5.4.x` (`JpaRepository`, `@Entity`, `@Table`, `@ManyToOne`, `@JoinColumn`, `Instant`, `LocalDate`) |
| **Banco de Dados** | PostgreSQL | SGBD relacional (porta padrão `5432`) |
| **Mapeamento DTO ↔ Entity** | ModelMapper | `2.3.0` (conversão entre entidades e objetos de transferência) |
| **Produtividade / Boilerplate** | Lombok | `1.18.30` (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@RequiredArgsConstructor`) |
| **Documentação Interativa** | Springfox Swagger 2 / Swagger UI | `2.9.2` (`@ApiOperation`, `@ApiResponse`, `@ApiModelProperty`) |
| **Build & Gerenciamento** | Maven | Gerenciamento de dependências e empacotamento (`spring-boot-maven-plugin`) |

## Arquitetura e Estrutura de Pacotes

A aplicação segue a **arquitetura em 3 camadas** (`Controller` $\rightarrow$ `Service` $\rightarrow$ `Repository`), utilizando associações unidirecionais e entidades intermediárias (`FuncionarioProjeto`, `TarefaEtiqueta` e `ResponsavelTarefa`) para evitar relacionamentos `N:N` diretos:

```text
src/main/java/br/edu/ifsudestemg/kb/
├── api/
│   ├── controller/      # Endpoints REST (@RestController), CORS e tratamento HTTP
│   └── dto/             # Objetos de transferência (<Entidade>DTO) com ModelMapper
├── config/              # Configurações da aplicação (ex.: SwaggerConfig)
├── exception/           # Exceções de domínio (RegraNegocioException)
├── model/
│   ├── entity/          # Entidades JPA (@Entity) e mapeamento relacional
│   └── repository/      # Interfaces Spring Data JPA (JpaRepository)
└── service/             # Regras de negócio, validações (@Transactional) e cascatas
```

### Fluxo de Requisição

```text
HTTP Request ─▶ @RestController ─▶ @Service (validar + regras) ─▶ @Repository ─▶ PostgreSQL
HTTP Response ◀─ DTO.create()   ◀─ Entity                       ◀─ Entity
```

### Modelo Relacional (Tabelas no PostgreSQL)

As entidades JPA são persistidas nas seguintes tabelas relacionais:

* **Cadastros Base:** `area_projeto`, `cargo`, `prioridade`, `funcionario`
* **Núcleo do Kanban:** `projeto`, `fase`, `tarefa`, `etiqueta`, `anotacao`
* **Tabelas Associativas:** `funcionario_projeto`, `responsavel_tarefa`, `tarefa_etiqueta`

## Pré-requisitos

Para executar o projeto localmente, certifique-se de possuir:

* **Java Development Kit (JDK) 17** instalado e configurado no `PATH` / `JAVA_HOME`.
* **Maven 3.8+** (ou wrapper `./mvnw` incluído no repositório).
* **PostgreSQL** em execução localmente (porta padrão `5432`).

## Configuração e Execução

1. **Acesse o diretório raiz do backend:**

   ```bash
   cd caminho/para/o/backend
   ```

2. **Configure o banco de dados PostgreSQL:**
   Crie o banco de dados correspondente e ajuste as credenciais no arquivo `src/main/resources/application.properties` (ou `application.yml`):

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/kb
   spring.datasource.username=postgres
   spring.datasource.password=sua_senha

   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   server.port=8080
   ```

3. **Compile e execute a aplicação via Maven:**

   ```bash
   mvn spring-boot:run
   ```

   *(Ou, utilizando o Maven Wrapper: `./mvnw spring-boot:run`)*

4. **Acesse a API e a documentação Swagger:**
   * **Base URL da API:** `http://localhost:8080/api/v1`
   * **Swagger UI:** `http://localhost:8080/swagger-ui.html`

## Mapa de Endpoints da API (`/api/v1`)

| Recurso | Endpoint Base | Métodos Suportados |
| :--- | :--- | :--- |
| **Áreas de Projeto** | `/api/v1/areas-projeto` | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| **Cargos** | `/api/v1/cargos` | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| **Prioridades** | `/api/v1/prioridades` | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| **Funcionários** | `/api/v1/funcionarios` | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| **Projetos** | `/api/v1/projetos` | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| **Participações no Projeto** | `/api/v1/funcionarios-projeto` | `GET /projeto/{projetoId}`, `GET /funcionario/{funcionarioId}`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| **Fases** | `/api/v1/fases` | `GET /projeto/{projetoId}`, `GET /{id}`, `POST`, `PUT /{id}`, `PUT /{id}/mover-esquerda`, `PUT /{id}/mover-direita`, `DELETE /{id}?confirmarExclusaoTarefas={bool}` |
| **Etiquetas** | `/api/v1/etiquetas` | `GET /projeto/{projetoId}`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| **Tarefas** | `/api/v1/tarefas` | `GET`, `GET /projeto/{projetoId}`, `GET /fase/{faseId}`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| **Etiquetas da Tarefa** | `/api/v1/tarefas-etiquetas` | `GET /tarefa/{tarefaId}`, `GET /etiqueta/{etiquetaId}`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| **Responsáveis da Tarefa** | `/api/v1/responsaveis-tarefa` | `GET /tarefa/{tarefaId}`, `GET /funcionario-projeto/{fpId}`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| **Anotações** | `/api/v1/anotacoes` | `GET /tarefa/{tarefaId}`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |

## Convenções de Resposta e Tratamento de Erros

* **Datas e Horários:** Campos `LocalDate` são serializados em `YYYY-MM-DD`; campos `Instant` seguem o padrão ISO-8601 UTC (`YYYY-MM-DDTHH:mm:ssZ`).
* **Tratamento de Exceções (`RegraNegocioException`):**
  * `400 Bad Request`: Violação de validação de campos, limites de tamanho, formato inválido ou regra de negócio — retorna a mensagem descritiva em texto puro (`String`) no corpo da resposta.
  * `404 Not Found`: Recurso não localizado pelo identificador informado — retorna a mensagem de não encontrado em texto puro (`String`).