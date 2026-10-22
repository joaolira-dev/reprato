package dev.reprato.inventory;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class InventoryConcurrencyTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired InventoryController inventory;

    @Test
    void onlyTwoOfTenConcurrentReservationsCanTakeTwoAvailableKits() throws Exception {
        var start = new CountDownLatch(1);
        var ids = IntStream.range(0, 10).mapToObj(ignored -> UUID.randomUUID()).toList();
        try (var executor = Executors.newFixedThreadPool(10)) {
            var attempts = ids.stream().<Future<Boolean>>map(id -> executor.submit(() -> {
                start.await();
                try {
                    inventory.reserve(new InventoryController.ReserveRequest(id, 3L, 1));
                    return true;
                } catch (ResponseStatusException error) {
                    assertThat(error.getStatusCode().value()).isEqualTo(409);
                    return false;
                }
            })).toList();
            start.countDown();
            long accepted = 0;
            UUID reservedId = null;
            for (int index = 0; index < attempts.size(); index++) {
                if (attempts.get(index).get()) {
                    accepted++;
                    reservedId = ids.get(index);
                }
            }
            assertThat(accepted).isEqualTo(2);
            assertThat(stockOf(3L)).isZero();

            inventory.release(reservedId);
            inventory.release(reservedId);
            assertThat(stockOf(3L)).isEqualTo(1);
        }
    }

    private int stockOf(Long offerId) {
        return inventory.list().stream().filter(stock -> stock.offerId().equals(offerId))
            .findFirst().orElseThrow().available();
    }
}
