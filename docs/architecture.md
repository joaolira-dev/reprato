# Arquitetura

```text
Navegador ──> Catálogo ──> catálogo_db
    │
    ├───────> Estoque ───> estoque_db
    │
    └───────> Pedidos ───> pedidos_db
                  │  │
                  │  └──── consulta Catálogo (oferta e preço)
                  └─────── reserva no Estoque
```

O catálogo é dono da descrição e do preço. O estoque é dono da quantidade e impede reservas acima da disponibilidade. Pedidos registra a intenção do cliente e guarda uma cópia do preço no momento da reserva. Nenhum serviço acessa tabelas de outro serviço.

Na primeira entrega, a comunicação entre serviços é HTTP síncrono. O serviço de pedidos consulta o catálogo, grava um pedido pendente, solicita a reserva ao estoque e depois marca o pedido como reservado. O estoque bloqueia a linha da oferta durante a alteração. Um teste com dez chamadas concorrentes valida que duas unidades permitem só duas reservas.

**Falha conhecida:** se um serviço cair entre a reserva de estoque e a atualização do pedido, pode restar um pedido pendente ou uma reserva sem pedido confirmado. O sistema ainda não possui reconciliação automática. Essa lacuna será tratada na etapa de mensageria, com eventos, idempotência, caixa de saída transacional e fila de mensagens rejeitadas. Pagamento será apenas uma simulação, sem dados financeiros reais.

Não há autenticação nesta versão, portanto os serviços devem permanecer apenas no ambiente local. O código UUID do pedido é um acesso de demonstração, não um mecanismo de autorização adequado para produção.

Implantação AWS e custos ainda serão projetados e avaliados antes de criar recursos. O ambiente local usa Docker Compose.
