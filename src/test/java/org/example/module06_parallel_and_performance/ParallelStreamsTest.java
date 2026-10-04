package org.example.module06_parallel_and_performance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ParallelStreamsTest {

    @Test
    @DisplayName("Debe contrastar la corrupción de datos en ArrayList compartido vs safe parallel collect")
    void testParallelSafety() {
        int count = 10_000;

        // Recolección segura sin estado mutable manual
        List<Integer> safeList = ParallelStreamsWhenAndWhyNot.safeParallelCollect(count);
        assertThat(safeList).hasSize(count);

        // En ArrayList no sincronizado, la mutación concurrente causa ArrayIndexOutOfBoundsException
        // o corrupción severa de datos (pérdida de elementos o nulls)
        try {
            List<Integer> unsafeList = ParallelStreamsWhenAndWhyNot.dangerousSharedStateParallel(count);
            assertThat(unsafeList.size())
                    .as("Si no explotó por suerte, habrá sufrido pérdida de elementos por race condition")
                    .isNotEqualTo(count);
        } catch (ArrayIndexOutOfBoundsException | NullPointerException ex) {
            // Esperado: race condition al redimensionar el buffer interno de ArrayList concurrentemente
            assertThat(ex).isInstanceOfAny(ArrayIndexOutOfBoundsException.class, NullPointerException.class);
        }
    }

    @Test
    @DisplayName("Debe calcular primos concurrentemente en tareas CPU-bound de forma correcta")
    void testCpuBoundParallel() {
        // En los primeros 100 enteros hay exactamente 25 primos
        long primesCount = ParallelStreamsWhenAndWhyNot.countPrimesParallel(100);
        assertThat(primesCount).isEqualTo(25);
    }

    @Test
    @DisplayName("Debe verificar que el pool común de ForkJoinPool está disponible")
    void testForkJoinPoolInfo() {
        int parallelism = ParallelStreamsWhenAndWhyNot.getCommonPoolParallelism();
        assertThat(parallelism).isGreaterThanOrEqualTo(1);
    }
}
