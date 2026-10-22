# Reprato

**Uma segunda chance para comida boa.** Plataforma de estudo em que comércios publicam kits do dia e pessoas reservam para retirada. O foco técnico é aprender limites de microserviços, estoque concorrente e a evolução para eventos e AWS.

O produto e os estabelecimentos exibidos são fictícios. Não há venda, pagamento ou integração com negócios reais.

## Estado atual

- Vitrine web responsiva com ofertas, filtros, favoritos e reserva de teste.
- Serviço de catálogo: ofertas e dados de retirada.
- Serviço de estoque: unidades disponíveis e reserva com bloqueio transacional.
- Serviço de pedidos: consulta o catálogo, reserva no estoque e registra o pedido.
- Um PostgreSQL local com **três bases separadas**, uma por serviço.
- Teste de concorrência: dez pedidos simultâneos para duas unidades, sem sobrevenda.

Veja [arquitetura](docs/architecture.md) e [roteiro](docs/roadmap.md). Os recursos são adicionados em commits pequenos para mostrar a evolução do projeto.

## Rodar localmente

Requisitos: Java 21, Maven, Node 22, npm e Docker.

1. Na raiz do projeto, inicie o banco: `docker compose up -d postgres`.
2. Em três terminais diferentes, na raiz do projeto, inicie os serviços:
   - `mvn -s .mvn/public-settings.xml -pl services/catalog-service spring-boot:run`
   - `mvn -s .mvn/public-settings.xml -pl services/inventory-service spring-boot:run`
   - `mvn -s .mvn/public-settings.xml -pl services/order-service spring-boot:run`
3. Em `web/`, execute `npm ci` e `npm run dev`.
4. Abra `http://localhost:5174`.

O PostgreSQL fica exposto só em `127.0.0.1:55434`. Os serviços respondem nas portas 18081 (catálogo), 18082 (estoque) e 18083 (pedidos). O Vite encaminha `/api` para eles. As senhas do Compose são apenas de desenvolvimento; não use essa configuração em ambiente público.

Se o Docker local estiver configurado com um helper de credenciais indisponível, use um diretório temporário com `config.json` contendo `{"auths":{}}` e execute o Compose com `DOCKER_CONFIG=/caminho/temporario`.

## Conferir o fluxo

Reserve um kit na vitrine. A quantidade exibida deve cair. Abra **Minha reserva** para consultar o último pedido e cancelá-lo; a quantidade deve voltar. O código do pedido é um identificador de acesso de demonstração e deve ser tratado como privado.

Para validar o backend: `mvn -s .mvn/public-settings.xml test`. Para a interface, em `web/`, execute `npm run lint` e `npm run build`.

As ofertas e os estabelecimentos são fictícios. O texto “hoje” representa dados de demonstração fixos. Não há autenticação, pagamento, notificações ou retirada real.

## Imagens

As fotografias de demonstração foram obtidas no [Unsplash](https://unsplash.com/license) e ficam em `web/public/images/`: [pães](https://images.unsplash.com/photo-1509440159596-0249088772ff), [tigela](https://images.unsplash.com/photo-1546069901-ba9599a7e63c), [padaria](https://images.unsplash.com/photo-1604908552986-eb22824b2150) e [croissants](https://images.unsplash.com/photo-1555507036-ab1f4038808a). Elas ilustram ofertas fictícias e não representam produtos reais.
