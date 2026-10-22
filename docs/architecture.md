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

Na primeira entrega, a comunicação entre serviços é HTTP síncrono. Isso deixa explícitos os limites e as falhas de rede antes de introduzir mensageria. Na próxima etapa, eventos de pedido e estoque serão transportados por filas, com idempotência, caixa de saída transacional e fila de mensagens rejeitadas. Pagamento será apenas uma simulação, sem dados financeiros reais.

Implantação AWS e custos ainda serão projetados e avaliados antes de criar recursos. O ambiente local usa Docker Compose.
