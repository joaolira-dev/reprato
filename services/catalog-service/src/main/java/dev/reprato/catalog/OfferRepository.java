package dev.reprato.catalog;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findByActiveTrueOrderByIdAsc();
}
