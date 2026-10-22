package dev.reprato.catalog;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/offers")
public class OfferController {
    private final OfferRepository offers;

    OfferController(OfferRepository offers) { this.offers = offers; }

    @GetMapping
    public List<Offer> list() { return offers.findByActiveTrueOrderByIdAsc(); }

    @GetMapping("/{id}")
    public Offer get(@PathVariable Long id) {
        return offers.findById(id).filter(Offer::isActive)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Oferta não encontrada"));
    }
}
