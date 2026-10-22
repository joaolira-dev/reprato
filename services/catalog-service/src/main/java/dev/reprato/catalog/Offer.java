package dev.reprato.catalog;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "offers")
public class Offer {
    @Id
    private Long id;
    private String title;
    private String merchant;
    private String district;
    private String category;
    private String description;
    private String pickup;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String image;
    private boolean active;

    protected Offer() {}

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getMerchant() { return merchant; }
    public String getDistrict() { return district; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getPickup() { return pickup; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public String getImage() { return image; }
    public boolean isActive() { return active; }
}
