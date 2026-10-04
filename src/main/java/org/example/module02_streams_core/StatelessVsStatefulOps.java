package org.example.module02_streams_core;

import java.util.Comparator;
import java.util.List;

/**
 * MÓDULO 02 - OPERACIONES STATELESS VS STATEFUL
 * -----------------------------------------------------------------------------
 * En una entrevista técnica sobre Streams, una de las preguntas de mayor nivel es:
 * "¿Cuál es la diferencia de rendimiento y memoria entre operaciones intermedias
 * con estado (stateful) y sin estado (stateless)?"
 *
 * 1. Operaciones Stateless (Sin Estado):
 *    - Ejemplos: filter(), map(), mapToInt(), peek(), flatMap()
 *    - Cada elemento se procesa de forma totalmente independiente.
 *    - No requieren memoria proporcional al tamaño de la colección (O(1) memoria por elemento).
 *    - Ideales para flujos continuos o streaming masivo.
 *
 * 2. Operaciones Stateful (Con Estado):
 *    - Ejemplos: distinct(), sorted(), limit(), skip()
 *    - Necesitan recordar elementos anteriores o acumular todo el conjunto.
 *    - 'distinct()' debe almacenar un HashSet interno de elementos ya vistos.
 *    - 'sorted()' DEBE cargar TODOS los elementos en un buffer antes de emitir siquiera el primer elemento.
 *      (¡Llamar a sorted() sobre un stream infinito causa OutOfMemoryError!).
 */
public class StatelessVsStatefulOps {

    public record Employee(String name, String department, double salary) {}

    /**
     * Pipeline stateless puro:
     * O(1) memoria adicional. Fluye elemento por elemento.
     */
    public static List<String> processStateless(List<Employee> employees) {
        return employees.stream()
                .filter(e -> e.salary() > 50_000)      // Stateless
                .map(e -> e.name().toUpperCase())      // Stateless
                .toList();
    }

    /**
     * Pipeline con operaciones stateful:
     * Requiere buffers en memoria y sincronización de estado.
     */
    public static List<String> processWithStatefulOps(List<Employee> employees) {
        return employees.stream()
                .filter(e -> e.salary() > 30_000)                      // Stateless
                .sorted(Comparator.comparingDouble(Employee::salary).reversed()) // STATEFUL: bufferiza todo
                .map(Employee::department)                             // Stateless
                .distinct()                                            // STATEFUL: mantiene set de vistos
                .limit(3)                                              // STATEFUL: contador de corte
                .toList();
    }
}
