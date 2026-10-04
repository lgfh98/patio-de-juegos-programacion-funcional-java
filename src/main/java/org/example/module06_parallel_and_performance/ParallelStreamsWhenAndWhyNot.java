package org.example.module06_parallel_and_performance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

/**
 * MÓDULO 06 - PARALLEL STREAMS: CUÁNDO USARLOS Y CUÁNDO SON UNA TRAMPA MORTAL
 * -----------------------------------------------------------------------------
 * En entrevistas para Senior Backend Engineer, preguntar por Parallel Streams
 * es el filtro definitivo para distinguir a quien solo conoce la sintaxis de quien
 * comprende la arquitectura interna de la JVM.
 *
 * 1. ¿Cómo funciona por debajo?
 *    - Utiliza el ForkJoinPool.commonPool() compartido por TODA la JVM.
 *    - El número de hilos suele ser: Runtime.getRuntime().availableProcessors() - 1.
 *
 * 2. EL MODELO N * Q (Brian Goetz):
 *    - N = número de elementos.
 *    - Q = costo computacional por elemento.
 *    - REGLA GENERAL: N * Q > 10,000 para que el beneficio del paralelismo
 *      supere el overhead de partir (fork) y fusionar (join) las tareas.
 *
 * 3. LAS 4 REGLAS DE ORO DE LA INDUSTRIA (CUÁNDO NO USARLO):
 *    - REGLA 1: NUNCA usar en I/O bloqueante (llamadas HTTP, consultas a BD, lectura de archivos).
 *      Bloqueará los hilos del pool común, asfixiando a todo el microservicio.
 *    - REGLA 2: NUNCA mutar estado compartido (ej. list.add()) -> Race conditions y corrupción de datos.
 *    - REGLA 3: CUIDADO con ThreadLocals (Spring SecurityContext, MDC de logs se pierden en los hilos del worker).
 *    - REGLA 4: La fuente de datos importa. ArrayList e IntStream.range se dividen en O(1).
 *      LinkedList o Streams de I/O se dividen en O(N), arruinando el paralelismo.
 */
public class ParallelStreamsWhenAndWhyNot {

    /**
     * PELIGRO: Mutación de estado mutable compartido en un stream paralelo.
     * Arrojará elementos nulos, pérdida de datos o excepciones por race conditions.
     */
    public static List<Integer> dangerousSharedStateParallel(int count) {
        List<Integer> unsafeList = new ArrayList<>();

        // ANTI-PATRÓN: forEach con mutación concurrente sin sincronización
        IntStream.range(0, count)
                .parallel()
                .forEach(unsafeList::add);

        return unsafeList;
    }

    /**
     * SOLUCIÓN CORRECTA:
     * Dejar que el recolector de Streams (collect / toList) administre la concurrencia
     * de forma segura y sin locks explícitos.
     */
    public static List<Integer> safeParallelCollect(int count) {
        return IntStream.range(0, count)
                .parallel()
                .boxed()
                .toList(); // Seguro contra hilos
    }

    /**
     * CASO DE USO IDEAL:
     * Tareas intensivas en CPU (CPU-bound), sin I/O, sin efectos secundarios,
     * sobre un volumen de datos representativo.
     * Ejemplo: Comprobación de números primos en un rango numérico.
     */
    public static long countPrimesParallel(int max) {
        return IntStream.rangeClosed(2, max)
                .parallel()
                .filter(ParallelStreamsWhenAndWhyNot::isPrime)
                .count();
    }

    public static boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; (long) i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }

    public static int getCommonPoolParallelism() {
        return ForkJoinPool.commonPool().getParallelism();
    }
}
