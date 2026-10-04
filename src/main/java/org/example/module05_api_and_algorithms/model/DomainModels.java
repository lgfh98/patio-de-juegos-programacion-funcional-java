package org.example.module05_api_and_algorithms.model;

import java.time.Instant;
import java.util.List;

/**
 * Modelos de dominio inmutables utilizando Java Records para los escenarios de APIs.
 */
public class DomainModels {

    // MODELOS PARA ESCENARIO 1: DETECCIÓN DE ANOMALÍAS FINANCIERAS
    public enum TransactionStatus { APPROVED, DECLINED, CANCELLED }

    public record Transaction(
            String id,
            String accountId,
            double amount,
            Instant timestamp,
            TransactionStatus status,
            String merchantCategory
    ) {}

    public record AnomalyAlert(
            String transactionId,
            String accountId,
            double amount,
            double accountMean,
            double zScore,
            String reason
    ) {}

    public record AccountAnomalySummary(
            String accountId,
            int totalTransactions,
            double meanAmount,
            List<AnomalyAlert> anomalies
    ) {}

    // MODELOS PARA ESCENARIO 2: LIQUIDACIÓN DE PEDIDOS MULTI-VENDEDOR
    public record OrderItem(
            String sku,
            String sellerId,
            int quantity,
            double unitPrice,
            String category,
            double taxRate // Ej. 0.19 para 19%
    ) {}

    public record MultiVendorOrder(
            String orderId,
            String customerId,
            Instant orderDate,
            List<OrderItem> items
    ) {}

    public record SellerSettlement(
            String sellerId,
            double grossAmount,
            double totalTax,
            double platformCommission,
            double netPayout,
            int itemsCount,
            List<String> skusSold
    ) {}
}
