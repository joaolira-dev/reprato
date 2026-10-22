package dev.reprato.orders;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderRepository orders;
    private final RestClient catalog;
    private final RestClient inventory;

    OrderController(OrderRepository orders,
            @Value("${reprato.catalog-url}") String catalogUrl,
            @Value("${reprato.inventory-url}") String inventoryUrl) {
        this.orders = orders;
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(2_000);
        requestFactory.setReadTimeout(3_000);
        this.catalog = RestClient.builder().baseUrl(catalogUrl).requestFactory(requestFactory).build();
        this.inventory = RestClient.builder().baseUrl(inventoryUrl).requestFactory(requestFactory).build();
    }

    record CreateOrderRequest(@NotNull Long offerId, @Min(1) int quantity,
                              @NotBlank @Size(max = 100) String guestName) {}
    record OfferView(Long id, String title, String merchant, BigDecimal price) {}
    record ReserveRequest(UUID orderId, Long offerId, int quantity) {}
    record OrderView(UUID id, Long offerId, String title, String merchant, String guestName,
                     int quantity, BigDecimal unitPrice, BigDecimal total, String status, Instant createdAt) {
        static OrderView from(PurchaseOrder order) {
            return new OrderView(order.getId(), order.getOfferId(), order.getTitle(), order.getMerchant(),
                order.getGuestName(), order.getQuantity(), order.getUnitPrice(), order.getTotal(),
                order.getStatus(), order.getCreatedAt());
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderView create(@Valid @RequestBody CreateOrderRequest request) {
        OfferView offer;
        try {
            offer = catalog.get().uri("/api/offers/{id}", request.offerId()).retrieve().body(OfferView.class);
        } catch (RestClientResponseException error) {
            if (error.getStatusCode().value() == 404) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Oferta não encontrada");
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Catálogo indisponível");
        } catch (RestClientException error) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Catálogo indisponível");
        }
        if (offer == null || offer.price() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Catálogo retornou oferta inválida");
        }

        var order = orders.save(new PurchaseOrder(UUID.randomUUID(), offer.id(), offer.title(),
            offer.merchant(), request.guestName().trim(), request.quantity(), offer.price()));
        try {
            inventory.post().uri("/api/stock/reservations")
                .body(new ReserveRequest(order.getId(), order.getOfferId(), order.getQuantity()))
                .retrieve().toBodilessEntity();
        } catch (RestClientResponseException error) {
            if (error.getStatusCode().value() == 409) {
                order.setStatus("REJECTED");
                orders.save(order);
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Estoque insuficiente");
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Estoque indisponível; pedido pendente de reconciliação");
        } catch (RestClientException error) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Estoque indisponível; pedido pendente de reconciliação");
        }
        order.setStatus("RESERVED");
        return OrderView.from(orders.save(order));
    }

    @GetMapping("/{id}")
    public OrderView get(@PathVariable UUID id) { return OrderView.from(findOrder(id)); }

    @DeleteMapping("/{id}")
    public OrderView cancel(@PathVariable UUID id) {
        var order = findOrder(id);
        if (order.getStatus().equals("CANCELED")) return OrderView.from(order);
        if (!order.getStatus().equals("RESERVED")) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este pedido não pode ser cancelado");
        }
        try {
            inventory.post().uri("/api/stock/reservations/{id}/release", id).retrieve().toBodilessEntity();
        } catch (RestClientException error) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Não foi possível liberar o estoque");
        }
        order.setStatus("CANCELED");
        return OrderView.from(orders.save(order));
    }

    private PurchaseOrder findOrder(UUID id) {
        return orders.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
    }
}
