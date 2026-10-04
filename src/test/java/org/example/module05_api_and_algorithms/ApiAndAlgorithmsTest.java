package org.example.module05_api_and_algorithms;

import org.example.module05_api_and_algorithms.model.DomainModels.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApiAndAlgorithmsTest {

    @Test
    @DisplayName("Debe detectar anomalías financieras basadas en Z-Score estadístico por cuenta")
    void testAnomalyDetection() {
        var now = Instant.now();
        var transactions = List.of(
                // Cuenta ACC-1: Patrón habitual de 50.0 - 55.0, y una transacción anómala de 5000.0
                new Transaction("tx-1", "ACC-1", 50.0, now, TransactionStatus.APPROVED, "GROCERY"),
                new Transaction("tx-2", "ACC-1", 52.0, now, TransactionStatus.APPROVED, "RESTAURANT"),
                new Transaction("tx-3", "ACC-1", 48.0, now, TransactionStatus.APPROVED, "GAS"),
                new Transaction("tx-4", "ACC-1", 51.0, now, TransactionStatus.APPROVED, "COFFEE"),
                new Transaction("tx-5", "ACC-1", 5000.0, now, TransactionStatus.APPROVED, "LUXURY_JEWELRY"),
                new Transaction("tx-6", "ACC-1", 8000.0, now, TransactionStatus.DECLINED, "SUSPECT"), // No debe contar porque es DECLINED

                // Cuenta ACC-2: Transacciones normales estables
                new Transaction("tx-7", "ACC-2", 100.0, now, TransactionStatus.APPROVED, "UTILITIES"),
                new Transaction("tx-8", "ACC-2", 105.0, now, TransactionStatus.APPROVED, "STREAMING")
        );

        var detector = new AnomalyDetectorService();
        var reports = detector.detectAnomalies(transactions);

        assertThat(reports).hasSize(1);
        var acc1Report = reports.getFirst();
        assertThat(acc1Report.accountId()).isEqualTo("ACC-1");
        assertThat(acc1Report.totalTransactions()).isEqualTo(5);
        assertThat(acc1Report.anomalies()).hasSize(1);
        assertThat(acc1Report.anomalies().getFirst().transactionId()).isEqualTo("tx-5");
        assertThat(acc1Report.anomalies().getFirst().amount()).isEqualTo(5000.0);
    }

    @Test
    @DisplayName("Debe liquidar órdenes multi-vendedor con impuestos, comisiones y consolidación de SKUs")
    void testOrderSettlement() {
        var now = Instant.now();
        var orders = List.of(
                new MultiVendorOrder("ORD-101", "CUST-1", now, List.of(
                        new OrderItem("SKU-LAPTOP", "SELLER-TECH", 1, 1000.0, "ELECTRONICS", 0.19),
                        new OrderItem("SKU-BOOK-JAVA", "SELLER-BOOKS", 2, 25.0, "BOOKS", 0.05)
                )),
                new MultiVendorOrder("ORD-102", "CUST-2", now, List.of(
                        new OrderItem("SKU-MOUSE", "SELLER-TECH", 2, 50.0, "ELECTRONICS", 0.19),
                        new OrderItem("SKU-TSHIRT", "SELLER-FASHION", 3, 20.0, "FASHION", 0.19)
                ))
        );

        var settlementService = new OrderSettlementService();
        var settlements = settlementService.generateSettlements(orders);

        assertThat(settlements).hasSize(3);

        // SELLER-TECH vendió: 1 laptop (1000) + 2 mouse (100) = 1100 Gross
        // Tax: 1100 * 0.19 = 209.0
        // Commission (ELECTRONICS = 10%): 1100 * 0.10 = 110.0
        // Net: 1100 - 209 - 110 = 781.0
        var techSettlement = settlements.stream()
                .filter(s -> s.sellerId().equals("SELLER-TECH"))
                .findFirst()
                .orElseThrow();

        assertThat(techSettlement.grossAmount()).isEqualTo(1100.0);
        assertThat(techSettlement.totalTax()).isEqualTo(209.0);
        assertThat(techSettlement.platformCommission()).isEqualTo(110.0);
        assertThat(techSettlement.netPayout()).isEqualTo(781.0);
        assertThat(techSettlement.skusSold()).containsExactly("SKU-LAPTOP", "SKU-MOUSE");
    }

    @Test
    @DisplayName("Debe resolver el algoritmo Top-K elementos más frecuentes con desempate alfabético")
    void testTopKFrequent() {
        var rawTexts = List.of(
                "Java streams are powerful and fast",
                "Functional programming in Java makes Java code clean",
                "Streams in Java are expressive and functional"
        );

        var top3 = TopKFrequentElementsService.findTopKFrequent(rawTexts, 3);

        // Conteo esperado:
        // java: 4 veces
        // and: 2 veces
        // are: 2 veces
        // functional: 2 veces
        // in: 2 veces
        // streams: 2 veces
        // Para el top 3:
        // 1st: "java" (4)
        // 2nd: "and" (2, por orden alfabético antes que "are")
        // 3rd: "are" (2, por orden alfabético antes que "functional")

        assertThat(top3).hasSize(3);
        assertThat(top3.get(0).word()).isEqualTo("java");
        assertThat(top3.get(0).count()).isEqualTo(4);

        assertThat(top3.get(1).word()).isEqualTo("and");
        assertThat(top3.get(1).count()).isEqualTo(2);

        assertThat(top3.get(2).word()).isEqualTo("are");
        assertThat(top3.get(2).count()).isEqualTo(2);
    }

    @Test
    @DisplayName("Debe manejar excepciones controladamente con Either sin detener el Stream")
    void testExceptionHandlingWithEither() {
        var inputUris = List.of(
                "https://api.github.com/users",
                "not a valid url @@@",
                "https://google.com/search?q=java",
                "ftp://invalid host with spaces"
        );

        var result = FunctionalExceptionHandling.parseUrisSafely(inputUris);

        assertThat(result.successfulUris()).hasSize(2)
                .extracting(URI::toString)
                .containsExactly("https://api.github.com/users", "https://google.com/search?q=java");

        assertThat(result.errorMessages()).hasSize(2);
    }
}
