package dev.reprato.inventory;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "stock_reservations")
class StockReservation {
    @Id
    private UUID orderId;
    private Long offerId;
    private int quantity;
    private String status;

    protected StockReservation() {}

    StockReservation(UUID orderId, Long offerId, int quantity) {
        this.orderId = orderId;
        this.offerId = offerId;
        this.quantity = quantity;
        this.status = "RESERVED";
    }

    UUID getOrderId() { return orderId; }
    Long getOfferId() { return offerId; }
    int getQuantity() { return quantity; }
    String getStatus() { return status; }
    void release() { status = "RELEASED"; }
}
