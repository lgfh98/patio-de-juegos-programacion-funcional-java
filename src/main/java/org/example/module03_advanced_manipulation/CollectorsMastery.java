package org.example.module03_advanced_manipulation;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * MÓDULO 03 - COLLECTORS MASTERY: LA CAJA DE HERRAMIENTAS DE AGREGACIÓN
 * -----------------------------------------------------------------------------
 * En el desarrollo de APIs y microservicios, la clase java.util.stream.Collectors
 * es la herramienta más importante para transformar datos en DTOs, agrupaciones,
 * resúmenes analíticos y estructuras jerárquicas.
 *
 * CONCEPTOS CRÍTICOS PARA ENTREVISTAS Y PRODUCCIÓN:
 * 1. toMap y el bug fatal de Duplicate Key:
 *    - toMap(keyMapper, valueMapper) LANZA IllegalStateException si dos elementos tienen la misma llave.
 *    - SIEMPRE proveer una BinaryOperator mergeFunction: toMap(k, v, (existing, replacement) -> existing).
 *
 * 2. groupingBy y Downstream Collectors:
 *    - groupingBy no solo agrupa en listas. Al pasar un segundo argumento (Downstream Collector),
 *      permite contar, sumar, proyectar o buscar extremos por grupo en un solo recorrido O(N).
 *
 * 3. partitioningBy:
 *    - Partición binaria (true/false) optimizada.
 *
 * 4. Collectors.teeing (Java 12+):
 *    - Aplica dos collectors en paralelo sobre el mismo stream y combina sus resultados con una BiFunction.
 */
public class CollectorsMastery {

    public record SaleRecord(String sellerId, String category, double amount, boolean isInternational) {}

    /**
     * 1. toMap SEGURO contra colisiones de llaves.
     * Retorna un mapa del último monto registrado por cada vendedor,
     * resolviendo colisiones tomando el valor más reciente o el mayor.
     */
    public static Map<String, Double> getHighestSalePerSeller(List<SaleRecord> sales) {
        if (sales == null) return Map.of();

        return sales.stream()
                .collect(Collectors.toMap(
                        SaleRecord::sellerId,
                        SaleRecord::amount,
                        // MERGE FUNCTION: resuelve colisiones entre llaves duplicadas
                        Double::max
                ));
    }

    /**
     * 2. groupingBy con Downstream:
     * Calcula el ingreso total (Total Revenue) por cada categoría.
     */
    public static Map<String, Double> getTotalRevenueByCategory(List<SaleRecord> sales) {
        if (sales == null) return Map.of();

        return sales.stream()
                .collect(Collectors.groupingBy(
                        SaleRecord::category,
                        Collectors.summingDouble(SaleRecord::amount)
                ));
    }

    /**
     * 3. groupingBy con mapping:
     * Extrae una lista de sólo los IDs de vendedores únicos por categoría.
     */
    public static Map<String, Set<String>> getUniqueSellersByCategory(List<SaleRecord> sales) {
        if (sales == null) return Map.of();

        return sales.stream()
                .collect(Collectors.groupingBy(
                        SaleRecord::category,
                        Collectors.mapping(SaleRecord::sellerId, Collectors.toSet())
                ));
    }

    /**
     * 4. Agrupación Multinivel (Hierarchical Grouping):
     * Agrupa primero por 'es internacional' y luego por 'categoría', contando las ventas.
     */
    public static Map<Boolean, Map<String, Long>> countSalesByRegionAndCategory(List<SaleRecord> sales) {
        if (sales == null) return Map.of();

        return sales.stream()
                .collect(Collectors.groupingBy(
                        SaleRecord::isInternational,
                        Collectors.groupingBy(
                                SaleRecord::category,
                                Collectors.counting()
                        )
                ));
    }

    /**
     * 5. partitioningBy:
     * Separa las ventas en dos listas fijas: domésticas (false) e internacionales (true).
     */
    public static Map<Boolean, List<SaleRecord>> partitionByInternational(List<SaleRecord> sales) {
        if (sales == null) return Map.of();

        return sales.stream()
                .collect(Collectors.partitioningBy(SaleRecord::isInternational));
    }

    /**
     * 6. Collectors.teeing (Java 12+):
     * En UNA SOLA pasada por el stream, obtiene la venta mínima y la venta máxima,
     * empaquetándolas en un record.
     */
    public record MinMaxRange(double min, double max) {}

    public static MinMaxRange calculateMinMaxRange(List<SaleRecord> sales) {
        if (sales == null || sales.isEmpty()) {
            return new MinMaxRange(0.0, 0.0);
        }

        return sales.stream().collect(
                Collectors.teeing(
                        Collectors.minBy(Comparator.comparingDouble(SaleRecord::amount)),
                        Collectors.maxBy(Comparator.comparingDouble(SaleRecord::amount)),
                        (minOpt, maxOpt) -> new MinMaxRange(
                                minOpt.map(SaleRecord::amount).orElse(0.0),
                                maxOpt.map(SaleRecord::amount).orElse(0.0)
                        )
                )
        );
    }
}
