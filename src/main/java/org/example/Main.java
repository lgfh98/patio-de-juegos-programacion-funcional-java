package org.example;

import org.example.module01_foundations.ImperativeVsDeclarative;
import org.example.module05_api_and_algorithms.AnomalyDetectorService;
import org.example.module05_api_and_algorithms.OrderSettlementService;
import org.example.module05_api_and_algorithms.TopKFrequentElementsService;
import org.example.module05_api_and_algorithms.model.DomainModels.*;

import java.time.Instant;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("🚀 PATIO DE JUEGOS: PROGRAMACIÓN FUNCIONAL Y STREAMS EN JAVA (ENTERPRISE LAB)");
        System.out.println("================================================================================");

        // DEMO 1: Fundamentos
        System.out.println("\n[MÓDULO 01 & 02] Filtro y Proyección Declarativa:");
        var users = List.of(
                new ImperativeVsDeclarative.User("Ana", 28, true),
                new ImperativeVsDeclarative.User("Carlos", 16, true),
                new ImperativeVsDeclarative.User("Beatriz", 32, true)
        );
        var activeAdults = ImperativeVsDeclarative.getActiveAdultUserNamesDeclarative(users);
        System.out.println("  Usuarios adultos activos: " + activeAdults);

        // DEMO 2: Algoritmo de Detección de Anomalías (Fintech)
        System.out.println("\n[MÓDULO 05 - ESCENARIO 1] Algoritmo de Detección de Anomalías en Transacciones:");
        var now = Instant.now();
        var transactions = List.of(
                new Transaction("tx-1", "ACC-789", 45.0, now, TransactionStatus.APPROVED, "GROCERY"),
                new Transaction("tx-2", "ACC-789", 50.0, now, TransactionStatus.APPROVED, "CAFE"),
                new Transaction("tx-3", "ACC-789", 55.0, now, TransactionStatus.APPROVED, "FUEL"),
                new Transaction("tx-4", "ACC-789", 48.0, now, TransactionStatus.APPROVED, "CINEMA"),
                new Transaction("tx-5", "ACC-789", 5200.0, now, TransactionStatus.APPROVED, "LUXURY_WATCH")
        );
        var detector = new AnomalyDetectorService();
        var anomalyReports = detector.detectAnomalies(transactions);
        anomalyReports.forEach(report -> {
            System.out.printf("  Cuenta: %s | Transacciones analizadas: %d | Media: $%.2f%n",
                    report.accountId(), report.totalTransactions(), report.meanAmount());
            report.anomalies().forEach(alert ->
                    System.out.printf("    ⚠️  ALERTA: TxID=%s por $%.2f -> %s (Z-Score: %.2f)%n",
                            alert.transactionId(), alert.amount(), alert.reason(), alert.zScore()));
        });

        // DEMO 3: Liquidación Multi-Vendedor en E-Commerce
        System.out.println("\n[MÓDULO 05 - ESCENARIO 2] Liquidación Financiera Multi-Vendedor Marketplace:");
        var orders = List.of(
                new MultiVendorOrder("ORD-001", "CUST-A", now, List.of(
                        new OrderItem("MACBOOK-PRO", "APPLE-STORE", 1, 2400.0, "ELECTRONICS", 0.19),
                        new OrderItem("CLEAN-CODE-BOOK", "BOOK-HUB", 2, 40.0, "BOOKS", 0.05)
                )),
                new MultiVendorOrder("ORD-002", "CUST-B", now, List.of(
                        new OrderItem("MAGIC-MOUSE", "APPLE-STORE", 1, 100.0, "ELECTRONICS", 0.19)
                ))
        );
        var settlementService = new OrderSettlementService();
        var settlements = settlementService.generateSettlements(orders);
        settlements.forEach(s ->
                System.out.printf("  Vendedor: %-12s | Bruto: $%-8.2f | Impuestos: $%-7.2f | Comisión: $%-7.2f | Pago Neto: $%-8.2f | SKUs: %s%n",
                        s.sellerId(), s.grossAmount(), s.totalTax(), s.platformCommission(), s.netPayout(), s.skusSold()));

        // DEMO 4: Top-K Frequent Words
        System.out.println("\n[MÓDULO 05 - ESCENARIO 3] Algoritmo Top-K Frecuencias (Desempate Léxico):");
        var logLines = List.of(
                "error 500 database timeout",
                "warn 404 resource not found",
                "error 500 connection refused",
                "info 200 ok"
        );
        var topWords = TopKFrequentElementsService.findTopKFrequent(logLines, 3);
        topWords.forEach(w -> System.out.printf("  Palabra: '%-10s' -> Frecuencia: %d%n", w.word(), w.count()));

        System.out.println("\n================================================================================");
        System.out.println("✅ Para ejecutar los 23 tests de verificación unitaria, ejecuta: ./gradlew test");
        System.out.println("================================================================================\n");
    }
}
