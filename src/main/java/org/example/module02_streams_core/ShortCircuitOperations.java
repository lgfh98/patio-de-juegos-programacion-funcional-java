package org.example.module02_streams_core;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * MÓDULO 02 - OPERACIONES DE CORTO CIRCUITO (SHORT-CIRCUIT)
 * -----------------------------------------------------------------------------
 * Una operación de corto circuito es aquella que NO necesita procesar todos los elementos
 * de la colección para producir un resultado final o limitar el flujo.
 *
 * Principales operaciones de corto circuito:
 * - anyMatch(Predicate)   : Termina apenas encuentra el PRIMER match (true).
 * - allMatch(Predicate)   : Termina apenas encuentra el PRIMER elemento que NO cumpla (false).
 * - noneMatch(Predicate)  : Termina apenas encuentra el PRIMER elemento que cumpla (false).
 * - findFirst()           : Retorna Optional con el primer elemento encontrado y detiene la evaluación.
 * - findAny()             : En streams secuenciales suele comportarse como findFirst(), pero en
 *                           streams paralelos retorna el primero que termine en cualquier hilo (no determinista, pero más rápido).
 * - limit(maxSize)        : Operación intermedia que corta el pipeline al alcanzar el tamaño especificado.
 */
public class ShortCircuitOperations {

    /**
     * Demuestra que anyMatch se detiene inmediatamente tras encontrar el primer positivo,
     * ahorrando iteraciones innecesarias.
     */
    public static ShortCircuitAudit testAnyMatchEfficiency(List<String> items, String target) {
        AtomicInteger evaluatedCount = new AtomicInteger(0);

        boolean found = items.stream()
                .peek(item -> evaluatedCount.incrementAndGet())
                .anyMatch(item -> item.equalsIgnoreCase(target));

        return new ShortCircuitAudit(found, evaluatedCount.get());
    }

    /**
     * Demuestra la generación de Streams infinitos y cómo 'limit'
     * convierte un flujo teóricamente infinito en uno finito y seguro.
     */
    public static List<Integer> generateFirstFibonacciNumbers(int count) {
        // Stream.iterate con 2 argumentos crea un stream infinito: f(n) = f(n-1) + f(n-2)
        return Stream.iterate(new int[]{0, 1}, fib -> new int[]{fib[1], fib[0] + fib[1]})
                .limit(count) // SHORT-CIRCUIT: Evita el bucle infinito
                .map(fib -> fib[0])
                .toList();
    }

    /**
     * Demuestra la diferencia conceptual entre findFirst y findAny:
     * - findFirst respeta el orden de encuentro (encounter order).
     * - findAny es óptimo para pipelines concurrentes donde no importa cuál se procese primero.
     */
    public static Optional<String> findFirstOccurrence(List<String> words, String prefix) {
        return words.stream()
                .filter(w -> w.startsWith(prefix))
                .findFirst();
    }

    public record ShortCircuitAudit(boolean result, int elementsEvaluated) {}
}
