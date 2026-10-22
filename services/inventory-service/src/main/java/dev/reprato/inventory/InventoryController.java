package dev.reprato.inventory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/stock")
public class InventoryController {
    private final StockRepository stocks;
    private final StockReservationRepository reservations;

    InventoryController(StockRepository stocks, StockReservationRepository reservations) {
        this.stocks = stocks;
        this.reservations = reservations;
    }

    record StockView(Long offerId, int available) {}
    record ReserveRequest(@NotNull UUID orderId, @NotNull Long offerId, @Min(1) int quantity) {}
    record ReservationView(UUID orderId, Long offerId, int quantity, String status) {}

    @GetMapping
    public List<StockView> list() {
        return stocks.findAll().stream().sorted(Comparator.comparing(Stock::getOfferId))
            .map(stock -> new StockView(stock.getOfferId(), stock.getAvailable())).toList();
    }

    @PostMapping("/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public ReservationView reserve(@Valid @RequestBody ReserveRequest request) {
        var existing = reservations.findById(request.orderId());
        if (existing.isPresent()) {
            var reservation = existing.get();
            if (!reservation.getOfferId().equals(request.offerId()) || reservation.getQuantity() != request.quantity() || !reservation.getStatus().equals("RESERVED")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Pedido já possui outra reserva");
            }
            return view(reservation);
        }

        var stock = stocks.lockByOfferId(request.offerId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estoque não encontrado"));
        if (stock.getAvailable() < request.quantity()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Estoque insuficiente");
        }
        stock.decrease(request.quantity());
        var reservation = reservations.save(new StockReservation(request.orderId(), request.offerId(), request.quantity()));
        return view(reservation);
    }

    @PostMapping("/reservations/{orderId}/release")
    @Transactional
    public ReservationView release(@PathVariable UUID orderId) {
        var reservation = reservations.lockByOrderId(orderId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva não encontrada"));
        if (reservation.getStatus().equals("RESERVED")) {
            var stock = stocks.lockByOfferId(reservation.getOfferId()).orElseThrow();
            stock.increase(reservation.getQuantity());
            reservation.release();
        }
        return view(reservation);
    }

    private ReservationView view(StockReservation reservation) {
        return new ReservationView(reservation.getOrderId(), reservation.getOfferId(), reservation.getQuantity(), reservation.getStatus());
    }
}
