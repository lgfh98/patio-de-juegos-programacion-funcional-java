package org.example.module03_advanced_manipulation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AdvancedManipulationTest {

    @Test
    @DisplayName("Debe aplanar estructuras complejas con flatMap y navegar Optional anidados")
    void testFlatMap() {
        var orders = List.of(
                new FlatMapExplained.CustomerOrder("ORD-1", "user1@mail.com", List.of(
                        new FlatMapExplained.OrderItem("SKU-APPLE", 2, 1.5),
                        new FlatMapExplained.OrderItem("SKU-BANANA", 5, 0.8)
                )),
                new FlatMapExplained.CustomerOrder("ORD-2", "user2@mail.com", List.of(
                        new FlatMapExplained.OrderItem("SKU-APPLE", 1, 1.5),
                        new FlatMapExplained.OrderItem("SKU-CHERRY", 10, 2.0)
                ))
        );

        var uniqueSkus = FlatMapExplained.extractUniquePurchasedSkus(orders);
        assertThat(uniqueSkus).containsExactly("SKU-APPLE", "SKU-BANANA", "SKU-CHERRY");

        var words = FlatMapExplained.extractUniqueWords(List.of("Hello Functional World", "world of functional streams"));
        assertThat(words).containsExactlyInAnyOrder("hello", "functional", "world", "of", "streams");

        // Optional flatMap
        var profileWithZip = new FlatMapExplained.UserProfile("user-1",
                Optional.of(new FlatMapExplained.Address("Bogotá", Optional.of("110111"))));
        var profileWithoutZip = new FlatMapExplained.UserProfile("user-2",
                Optional.of(new FlatMapExplained.Address("Medellín", Optional.empty())));
        var profileWithoutAddress = new FlatMapExplained.UserProfile("user-3", Optional.empty());

        assertThat(FlatMapExplained.getPostalCodeSafely(profileWithZip)).contains("110111");
        assertThat(FlatMapExplained.getPostalCodeSafely(profileWithoutZip)).isEmpty();
        assertThat(FlatMapExplained.getPostalCodeSafely(profileWithoutAddress)).isEmpty();
    }

    @Test
    @DisplayName("Debe contrastar la reducción inmutable con la acumulación mutable")
    void testReduceVsCollect() {
        var amounts = List.of(15.5, 24.5, 60.0);
        double total = ReduceVsCollect.calculateTotalRevenue(amounts);
        assertThat(total).isEqualTo(100.0);

        var maxVal = ReduceVsCollect.findMaximumValue(List.of(12, 89, 45, 99, 23));
        assertThat(maxVal).contains(99);

        var collected = ReduceVsCollect.customCollectToList(List.of("A", "B", "C"));
        assertThat(collected).containsExactly("A", "B", "C");
    }

    @Test
    @DisplayName("Debe dominar agrupaciones complejas, particiones y teeing con Collectors")
    void testCollectorsMastery() {
        var sales = List.of(
                new CollectorsMastery.SaleRecord("seller-1", "Electronics", 500.0, true),
                new CollectorsMastery.SaleRecord("seller-2", "Electronics", 300.0, false),
                new CollectorsMastery.SaleRecord("seller-1", "Books", 50.0, false),
                new CollectorsMastery.SaleRecord("seller-3", "Books", 80.0, true),
                new CollectorsMastery.SaleRecord("seller-1", "Electronics", 1200.0, true) // Duplicado de seller-1
        );

        // 1. toMap con resolución de duplicados (mayor monto para seller-1)
        Map<String, Double> highestPerSeller = CollectorsMastery.getHighestSalePerSeller(sales);
        assertThat(highestPerSeller)
                .containsEntry("seller-1", 1200.0)
                .containsEntry("seller-2", 300.0)
                .containsEntry("seller-3", 80.0);

        // 2. groupingBy con summingDouble
        Map<String, Double> revenueByCategory = CollectorsMastery.getTotalRevenueByCategory(sales);
        assertThat(revenueByCategory)
                .containsEntry("Electronics", 2000.0) // 500 + 300 + 1200
                .containsEntry("Books", 130.0);       // 50 + 80

        // 3. groupingBy con mapping a Sets
        Map<String, Set<String>> sellersByCategory = CollectorsMastery.getUniqueSellersByCategory(sales);
        assertThat(sellersByCategory.get("Electronics"))
                .containsExactlyInAnyOrder("seller-1", "seller-2");

        // 4. partitioningBy
        Map<Boolean, List<CollectorsMastery.SaleRecord>> partitioned = CollectorsMastery.partitionByInternational(sales);
        assertThat(partitioned.get(true)).hasSize(3);
        assertThat(partitioned.get(false)).hasSize(2);

        // 5. teeing (mínimo y máximo en una sola pasada)
        CollectorsMastery.MinMaxRange range = CollectorsMastery.calculateMinMaxRange(sales);
        assertThat(range.min()).isEqualTo(50.0);
        assertThat(range.max()).isEqualTo(1200.0);
    }
}
