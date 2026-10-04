package org.example.module02_streams_core;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.IntStream;

/**
 * MÓDULO 02 - PRIMITIVE STREAMS Y RENDIMIENTO (Evitando el costo de Boxing)
 * -----------------------------------------------------------------------------
 * En Java, los genéricos operan sobre objetos (ej. Stream<Integer>).
 * Si procesamos 10 millones de enteros con Stream<Integer>, la JVM debe:
 * 1. Crear 10 millones de objetos Integer en el Heap (Auto-boxing: ~24 bytes c/u + punteros).
 * 2. Desempaquetarlos (Auto-unboxing) para cada suma/comparación.
 * 3. Provocar presión masiva en el Garbage Collector (GC).
 *
 * SOLUCIÓN DE ALTO RENDIMIENTO EN LA INDUSTRIA:
 * Los Streams Primitivos especializados:
 * - IntStream
 * - LongStream
 * - DoubleStream
 *
 * Trabajan directamente sobre primitivos (int, long, double) en registros/stack sin boxing.
 * Además ofrecen métodos numéricos directos: .sum(), .average(), .min(), .max(), .summaryStatistics().
 */
public class PrimitiveStreamsPerformance {

    /**
     * Calcula la suma de un rango utilizando IntStream sin boxing.
     */
    public static long calculateSumPrimitive(int start, int endInclusive) {
        return IntStream.rangeClosed(start, endInclusive)
                .asLongStream()
                .sum();
    }

    /**
     * Obtiene en una sola pasada (single-pass O(N)) el conteo, suma, mínimo, promedio y máximo.
     * Esencial en endpoints analíticos de métricas o telemetría.
     */
    public static IntSummaryStatistics calculateMetrics(List<Integer> values) {
        if (values == null || values.isEmpty()) {
            return new IntSummaryStatistics();
        }

        return values.stream()
                .mapToInt(Integer::intValue) // Transición eficiente de Stream<Integer> a IntStream
                .summaryStatistics();
    }

    /**
     * Demuestra el uso de range para generar algoritmos basados en índices
     * sin necesidad de un bucle 'for (int i = 0; i < n; i++)'.
     */
    public static List<String> pairIndicesWithValues(List<String> items) {
        return IntStream.range(0, items.size())
                .mapToObj(i -> "[" + i + "] " + items.get(i)) // De IntStream de vuelta a Stream<String>
                .toList();
    }
}
