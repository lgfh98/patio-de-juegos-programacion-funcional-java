package org.example.module02_streams_core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.IntSummaryStatistics;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StreamsCoreTest {

    @Test
    @DisplayName("Debe verificar que las operaciones intermedias no se ejecutan sin operación terminal")
    void testLazyEvaluationWithoutTerminalOp() {
        var names = List.of("Ana", "Alexander", "Bob", "Guillermo");
        int executedCount = StreamLifecycleAndPipelines.demonstrateLazinessWithoutTerminalOp(names);

        // Sin operación terminal, el motor no procesa nada
        assertThat(executedCount).isZero();
    }

    @Test
    @DisplayName("Debe comprobar la fusión de bucles y la ejecución bajo demanda al haber operación terminal")
    void testLoopFusionExecution() {
        var names = List.of("Ana", "Alexander", "Bob", "Guillermo", "Valeria");
        var pipelineResult = StreamLifecycleAndPipelines.demonstrateLoopFusionExecution(names);

        assertThat(pipelineResult.result())
                .containsExactly("ALEXANDER", "GUILLERMO");

        // El pipeline solo procesó hasta encontrar 2 elementos que cumplieron (gracias a limit(2))
        // "Valeria" nunca fue procesada en filter ni en map!
        assertThat(pipelineResult.auditLog())
                .contains("filter: Alexander", "map: Alexander", "filter: Guillermo", "map: Guillermo")
                .doesNotContain("filter: Valeria", "map: Valeria");
    }

    @Test
    @DisplayName("Debe lanzar IllegalStateException si se intenta reutilizar un Stream consumido")
    void testStreamReuseThrowsException() {
        assertThatThrownBy(StreamLifecycleAndPipelines::demonstrateStreamReuseThrowsException)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("stream has already been operated upon or closed");
    }

    @Test
    @DisplayName("Debe procesar operaciones con y sin estado de forma precisa")
    void testStatelessVsStateful() {
        var employees = List.of(
                new StatelessVsStatefulOps.Employee("Mario", "IT", 60_000),
                new StatelessVsStatefulOps.Employee("Lucia", "Finance", 75_000),
                new StatelessVsStatefulOps.Employee("Pedro", "IT", 45_000),
                new StatelessVsStatefulOps.Employee("Sofia", "Marketing", 80_000),
                new StatelessVsStatefulOps.Employee("Jorge", "Finance", 90_000)
        );

        var topDepts = StatelessVsStatefulOps.processWithStatefulOps(employees);

        // Ordenados por salario descendente:
        // Jorge (90k - Finance), Sofia (80k - Marketing), Lucia (75k - Finance), Mario (60k - IT), Pedro (45k - IT)
        // Distinct de depts en ese orden: Finance, Marketing, IT. Limit(3) => [Finance, Marketing, IT]
        assertThat(topDepts).containsExactly("Finance", "Marketing", "IT");
    }

    @Test
    @DisplayName("Debe demostrar el corto circuito en anyMatch y la generación de Fibonacci")
    void testShortCircuit() {
        var items = List.of("Alpha", "Bravo", "Charlie", "Delta", "Echo");

        var audit = ShortCircuitOperations.testAnyMatchEfficiency(items, "Bravo");
        assertThat(audit.result()).isTrue();
        // Solo evaluó 2 elementos (Alpha, Bravo) y cortó inmediatamente!
        assertThat(audit.elementsEvaluated()).isEqualTo(2);

        // Fibonacci con Stream.iterate + limit
        var fibs = ShortCircuitOperations.generateFirstFibonacciNumbers(7);
        assertThat(fibs).containsExactly(0, 1, 1, 2, 3, 5, 8);
    }

    @Test
    @DisplayName("Debe calcular métricas numéricas usando IntStream sin costo de autoboxing")
    void testPrimitiveStreams() {
        long sum = PrimitiveStreamsPerformance.calculateSumPrimitive(1, 100);
        assertThat(sum).isEqualTo(5050); // Fórmula de Gauss: n*(n+1)/2 = 100*101/2 = 5050

        IntSummaryStatistics stats = PrimitiveStreamsPerformance.calculateMetrics(List.of(10, 20, 30, 40, 50));
        assertThat(stats.getCount()).isEqualTo(5);
        assertThat(stats.getSum()).isEqualTo(150);
        assertThat(stats.getMin()).isEqualTo(10);
        assertThat(stats.getMax()).isEqualTo(50);
        assertThat(stats.getAverage()).isEqualTo(30.0);

        var indexed = PrimitiveStreamsPerformance.pairIndicesWithValues(List.of("A", "B", "C"));
        assertThat(indexed).containsExactly("[0] A", "[1] B", "[2] C");
    }
}
