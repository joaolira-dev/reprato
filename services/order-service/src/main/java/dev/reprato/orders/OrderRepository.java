package dev.reprato.orders;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface OrderRepository extends JpaRepository<PurchaseOrder, UUID> {}
