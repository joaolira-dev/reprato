package dev.reprato.inventory;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "stock")
class Stock {
    @Id
    private Long offerId;
    private int available;

    protected Stock() {}

    Long getOfferId() { return offerId; }
    int getAvailable() { return available; }
    void decrease(int quantity) { available -= quantity; }
    void increase(int quantity) { available += quantity; }
}
