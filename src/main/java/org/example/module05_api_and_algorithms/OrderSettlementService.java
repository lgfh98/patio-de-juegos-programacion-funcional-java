package org.example.module05_api_and_algorithms;

import org.example.module05_api_and_algorithms.model.DomainModels.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * ESCENARIO 2: LIQUIDACIÓN FINANCIERA MULTI-VENDEDOR EN E-COMMERCE
 * -----------------------------------------------------------------------------
 * En una plataforma tipo marketplace (Amazon, MercadoLibre), un carrito u orden
 * puede contener productos vendidos por diferentes comerciantes (sellers).
 *
 * Requerimiento de API:
 * Endpoint que toma una lista de órdenes y genera el reporte de liquidación financiera
 * para cada vendedor:
 * - Aplanar todos los items contenidos en las órdenes (flatMap).
 * - Calcular comisiones de plataforma basadas en reglas por categoría (reglas declarativas).
 * - Agrupar por vendedor (groupingBy).
 * - Calcular Monto Bruto, Impuestos, Comisión y Pago Neto (gross - tax - commission).
 * - Recolectar lista única de SKUs vendidos.
 * - Ordenar el resultado por mayor pago neto descendente.
 */
public class OrderSettlementService {

    // Regla de comisión funcional configurable según la categoría del producto
    private static final Map<String, Double> COMMISSION_RATES = Map.of(
            "ELECTRONICS", 0.10, // 10% de comisión
            "FASHION",     0.15, // 15% de comisión
            "BOOKS",       0.05  // 5% de comisión
    );

    private static final double DEFAULT_COMMISSION_RATE = 0.08; // 8% por defecto

    public List<SellerSettlement> generateSettlements(List<MultiVendorOrder> orders) {
        if (orders == null || orders.isEmpty()) {
            return List.of();
        }

        // 1. Aplanar las órdenes a un stream continuo de items
        Map<String, List<OrderItem>> itemsBySeller = orders.stream()
                .flatMap(order -> order.items().stream())
                .collect(Collectors.groupingBy(OrderItem::sellerId));

        // 2. Procesar la liquidación por vendedor
        return itemsBySeller.entrySet().stream()
                .map(entry -> buildSellerSettlement(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingDouble(SellerSettlement::netPayout).reversed())
                .toList();
    }

    private SellerSettlement buildSellerSettlement(String sellerId, List<OrderItem> items) {
        // Cálculo de montos en una sola pasada usando stream y reduce/summing
        double grossAmount = items.stream()
                .mapToDouble(item -> item.quantity() * item.unitPrice())
                .sum();

        double totalTax = items.stream()
                .mapToDouble(item -> (item.quantity() * item.unitPrice()) * item.taxRate())
                .sum();

        double platformCommission = items.stream()
                .mapToDouble(item -> {
                    double itemTotal = item.quantity() * item.unitPrice();
                    double rate = COMMISSION_RATES.getOrDefault(item.category().toUpperCase(), DEFAULT_COMMISSION_RATE);
                    return itemTotal * rate;
                })
                .sum();

        double netPayout = grossAmount - totalTax - platformCommission;

        int totalItemsSold = items.stream()
                .mapToInt(OrderItem::quantity)
                .sum();

        List<String> uniqueSkus = items.stream()
                .map(OrderItem::sku)
                .distinct()
                .sorted()
                .toList();

        return new SellerSettlement(
                sellerId,
                round(grossAmount),
                round(totalTax),
                round(platformCommission),
                round(netPayout),
                totalItemsSold,
                uniqueSkus
        );
    }

    private double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }
}
