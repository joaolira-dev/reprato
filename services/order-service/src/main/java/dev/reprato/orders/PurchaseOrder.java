package dev.reprato.orders;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "purchase_orders")
class PurchaseOrder {
    @Id
    private UUID id;
    private Long offerId;
    private String title;
    private String merchant;
    private String guestName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal total;
    private String status;
    private Instant createdAt;

    protected PurchaseOrder() {}

    PurchaseOrder(UUID id, Long offerId, String title, String merchant, String guestName, int quantity, BigDecimal unitPrice) {
        this.id = id;
        this.offerId = offerId;
        this.title = title;
        this.merchant = merchant;
        this.guestName = guestName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.total = unitPrice.multiply(BigDecimal.valueOf(quantity));
        this.status = "PENDING_STOCK";
        this.createdAt = Instant.now();
    }

    UUID getId() { return id; }
    Long getOfferId() { return offerId; }
    String getTitle() { return title; }
    String getMerchant() { return merchant; }
    String getGuestName() { return guestName; }
    int getQuantity() { return quantity; }
    BigDecimal getUnitPrice() { return unitPrice; }
    BigDecimal getTotal() { return total; }
    String getStatus() { return status; }
    Instant getCreatedAt() { return createdAt; }
    void setStatus(String status) { this.status = status; }
}
