# Microserviço de Gestão de Pedidos

Este projeto é um microserviço de back-end para um sistema B2B de gestão de pedidos. A API RESTful permite que parceiros comerciais criem, consultem, atualizem e cancelem pedidos. O sistema inclui um mecanismo de controle de crédito para parceiros e notifica as mudanças de status de forma assíncrona usando Apache Kafka.

O projeto foi desenvolvido com foco em cenários de alta concorrência, escalabilidade e performance.

---

## Tecnologias Utilizadas

- **Java 17**: Versão da linguagem Java.
- **Spring Boot 3**: Framework principal para a construção da aplicação.
- **Spring Data JPA**: Para persistência de dados e abstração de repositórios.
- **PostgreSQL**: Banco de dados relacional para armazenar os dados dos pedidos e parceiros.
- **Flyway**: Ferramenta para versionamento e migração de esquema de banco de dados.
- **Apache Kafka**: Plataforma de streaming de eventos para notificações assíncronas.
- **Docker & Docker Compose**: Para containerização e orquestração do ambiente de desenvolvimento e produção.
- **Gradle**: Ferramenta de automação de build.
- **Swagger/OpenAPI**: Para documentação interativa da API REST.
- **Lombok**: Para reduzir código boilerplate em classes de modelo e DTOs.

---

## Pré-requisitos

Para executar este projeto, você precisará ter as seguintes ferramentas instaladas em sua máquina:

- **JDK 17** (ou superior)
- **Docker**
- **Docker Compose**

---

## Como Executar o Projeto

O ambiente completo (Aplicação, Banco de Dados PostgreSQL e Kafka) é orquestrado pelo Docker Compose. Para iniciar tudo, siga os passos abaixo.

1.  **Clone o repositório** (se ainda não o fez):
    ```bash
    git clone <url-do-repositorio>
    cd gestaopedidos
    ```

2.  **Compile a aplicação**:
    O `Dockerfile` espera que o arquivo `.jar` da aplicação já esteja construído. Execute o seguinte comando para compilar o projeto e gerar o artefato:
    ```bash
    ./gradlew clean build
    ```

3.  **Inicie os contêineres**:
    Com o `.jar` gerado, agora você pode iniciar o ambiente com o Docker Compose:
    ```bash
    docker-compose up -d
    ```
    - `-d`: Executa os contêineres em modo "detached" (em segundo plano).

    A primeira execução pode levar alguns minutos, pois o Docker precisará baixar as imagens do PostgreSQL e Kafka.

4.  **Verifique se os serviços estão rodando**:
    Você pode usar o comando `docker-compose ps` para ver o status dos contêineres. Todos devem estar com o status `Up` ou `running`.

A aplicação estará disponível na porta `8080` da sua máquina local.

---

## Documentação da API (Swagger)

Após iniciar a aplicação, a documentação interativa da API, gerada pelo Swagger, estará disponível no seguinte endereço:

- **URL do Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Através dessa interface, é possível visualizar todos os endpoints, seus parâmetros, e até mesmo testar a API diretamente do navegador.

### Principais Endpoints

- `POST /api/v1/pedidos`: Cria um novo pedido.
- `GET /api/v1/pedidos/{id}`: Consulta um pedido específico pelo seu ID.
- `GET /api/v1/pedidos`: Lista todos os pedidos com suporte a filtros por data e status.
- `PUT /api/v1/pedidos/{id}/status`: Atualiza o status de um pedido.
- `PUT /api/v1/pedidos/{id}/cancelar`: Cancela um pedido.

---

## Banco de Dados e Migrações

- O serviço do PostgreSQL é executado no contêiner `gestaopedidos_db` e expõe a porta `5432`.
- As credenciais e o nome do banco de dados estão definidos no arquivo `docker-compose.yml`.
- O esquema do banco de dados é gerenciado pelo **Flyway**. Os scripts de migração estão localizados em `src/main/resources/db/migration`. O Flyway aplica automaticamente as migrações pendentes durante a inicialização da aplicação.

---

## Notificações com Kafka

- O serviço do Kafka é executado no contêiner `kafka`.
- Quando o status de um pedido é alterado (criação, atualização, cancelamento), uma mensagem é enviada para o tópico `notificacoes-pedidos`.
- O projeto inclui um serviço de consumidor (`KafkaConsumerService`) que escuta este tópico e imprime as mensagens recebidas no console do contêiner da aplicação (`gestaopedidos_app`), servindo como uma demonstração da integração.
- Para ver os logs das notificações, você pode executar:
  ```bash
  docker logs -f gestaopedidos_app
  ```

---

## Como Parar a Aplicação

Para parar e remover todos os contêineres criados pelo Docker Compose, execute o seguinte comando no diretório raiz do projeto:

```bash
docker-compose down
```
