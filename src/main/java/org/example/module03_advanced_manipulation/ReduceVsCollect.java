package org.example.module03_advanced_manipulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MÓDULO 03 - REDUCE VS COLLECT (Plegado Inmutable vs Acumulación Mutable)
 * -----------------------------------------------------------------------------
 * PREGUNTA CLÁSICA DE ARQUITECTURA / ENTREVISTA:
 * "¿Cuándo deberías usar reduce() y cuándo collect()?"
 *
 * 1. REDUCE (Plegado Funcional Inmutable / Fold):
 *    - Firma: reduce(identity, BinaryOperator<T> accumulator)
 *    - Diseñado para operar sobre VALORES INMUTABLES (enteros, doubles, strings, BigDecimals).
 *    - En cada paso combina dos valores y produce un NUEVO valor.
 *    - Casos ideales: sumas, multiplicaciones, mínimo/máximo, concatenación matemática.
 *    - ANTI-PATRÓN GRAVE: Usar reduce() para acumular en una colección (ej. lista).
 *      Hacer 'acc.add(elem); return acc;' en reduce rompe los contratos de inmutabilidad y
 *      si se crea una nueva lista en cada paso 'new ArrayList(acc)', tiene costo cuadrático O(N^2)
 *      y colapsa la memoria.
 *
 * 2. COLLECT (Reducción Mutable / Mutable Container Accumulation):
 *    - Firma: collect(Supplier, BiConsumer accumulator, BiConsumer combiner)
 *    - Diseñado explícitamente para acumular elementos eficientemente en un CONTENEDOR MUTABLE
 *      (ej. ArrayList, HashSet, HashMap, StringBuilder).
 *    - Muta el contenedor en sitio (in-place) con costo lineal O(N).
 */
public class ReduceVsCollect {

    /**
     * Uso correcto de reduce: Reducción matemática inmutable (suma ponderada).
     */
    public static double calculateTotalRevenue(List<Double> transactionAmounts) {
        if (transactionAmounts == null) return 0.0;

        // identity = 0.0 (elemento neutro para la suma)
        // accumulator = Double::sum
        return transactionAmounts.stream()
                .reduce(0.0, Double::sum);
    }

    /**
     * Reducción sin valor identidad: Retorna Optional<T>
     * ya que la lista podría estar vacía.
     */
    public static Optional<Integer> findMaximumValue(List<Integer> numbers) {
        if (numbers == null) return Optional.empty();

        return numbers.stream()
                .reduce(Integer::max);
    }

    /**
     * Acumulación mutable eficiente con collect:
     * Construye un contenedor acumulativo sin crear listas intermedias basura.
     */
    public static List<String> customCollectToList(List<String> source) {
        return source.stream()
                .collect(
                        ArrayList::new,       // Supplier: provee contenedor mutable
                        ArrayList::add,       // Accumulator: agrega elemento al contenedor
                        ArrayList::addAll     // Combiner: combina dos contenedores en caso de paralelismo
                );
    }
}
