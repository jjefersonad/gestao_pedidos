package br.com.gestaopedidos.common;

public final class AppConstants {

    private AppConstants() {
        // restrict instantiation
    }

    // PedidoService messages
    public static final String PARCEIRO_NAO_ENCONTRADO = "Parceiro não encontrado";
    public static final String CREDITO_INSUFICIENTE = "Crédito insuficiente";
    public static final String PEDIDO_NAO_ENCONTRADO = "Pedido não encontrado";

    // NotificacaoService messages
    public static final String NOTIFICACAO_MUDANCA_STATUS = "Pedido %d teve seu status alterado para %s";

    // Kafka topics
    public static final String KAFKA_TOPIC_NOTIFICACOES_PEDIDOS = "notificacoes-pedidos";

    // Swagger/OpenAPI tags
    public static final String SWAGGER_TAG_PEDIDOS_NAME = "Pedidos";
    public static final String SWAGGER_TAG_PEDIDOS_DESCRIPTION = "Rotas de Gestão de Pedidos";

    // Swagger/OpenAPI operation summaries
    public static final String SWAGGER_SUMMARY_CADASTRO_PEDIDOS = "Cadastro de pedidos";
    public static final String SWAGGER_SUMMARY_DETALHE_PEDIDO = "Detalhe do pedido";
    public static final String SWAGGER_SUMMARY_LISTA_PEDIDOS = "Lista de pedidos";
    public static final String SWAGGER_SUMMARY_ATUALIZA_STATUS_PEDIDO = "Atualiza status de um pedido";
    public static final String SWAGGER_SUMMARY_CANCELAR_PEDIDO = "Cancelar pedido";

    // Swagger/OpenAPI response descriptions
    public static final String SWAGGER_RESPONSE_PEDIDO_CADASTRADO = "Retorna o pedido cadastrado";
    public static final String SWAGGER_RESPONSE_DETALHE_PEDIDO = "Retorna todos os dados de um único pedido";
    public static final String SWAGGER_RESPONSE_LISTA_PEDIDOS = "Retorna uma lista paginada com todos os pedidos";

    // Swagger/OpenAPI parameter descriptions
    public static final String SWAGGER_PARAM_PEDIDO_ID_DESCRIPTION = "ID do pedido";
    public static final String SWAGGER_PARAM_PERIODO_INICIO_DESCRIPTION = "Data e hora de início do período (formato ISO: yyyy-MM-ddTHH:mm:ss)";
    public static final String SWAGGER_PARAM_PERIODO_FIM_DESCRIPTION = "Data e hora de fim do período (formato ISO: yyyy-MM-ddTHH:mm:ss)";
    public static final String SWAGGER_PARAM_STATUS_PEDIDO_DESCRIPTION = "Status do pedido (PENDENTE, APROVADO, EM_PROCESSAMENTO, ENVIADO, ENTREGUE, CANCELADO)";
    public static final String SWAGGER_PARAM_PAGE_DESCRIPTION = "Número da página (0-indexed)";
    public static final String SWAGGER_PARAM_SIZE_DESCRIPTION = "Tamanho da página";
    public static final String SWAGGER_PARAM_SORT_DESCRIPTION = "Critério de ordenação (ex: campo,asc ou campo,desc)";

}