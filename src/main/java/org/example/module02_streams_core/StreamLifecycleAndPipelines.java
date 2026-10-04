package org.example.module02_streams_core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * MÓDULO 02 - CICLO DE VIDA DE UN STREAM Y EVALUACIÓN PEREZOSA (LAZY EVALUATION)
 * -----------------------------------------------------------------------------
 * Un Stream NO es una estructura de datos. No almacena elementos.
 * Es un conducto (pipeline) que transporta elementos desde una fuente (Source),
 * los transforma a través de Operaciones Intermedias (Lazy) y produce un resultado
 * o efecto final mediante una Operación Terminal (Eager).
 *
 * TRES FASES CLAVE:
 * 1. Source: Collection.stream(), Arrays.stream(), Stream.of(), Stream.iterate()
 * 2. Intermediate Operations: filter(), map(), peek()... RETORNAN OTRO STREAM. Son perezosas.
 * 3. Terminal Operation: toList(), collect(), count(), forEach(), reduce()... CIERRAN EL STREAM.
 *
 * PREGUNTA TÍPICA DE ENTREVISTA:
 * "¿Qué imprime este código si no tiene operación terminal?"
 * Stream.of("a", "b", "c").filter(s -> { System.out.println(s); return true; });
 * RESPUESTA: NO IMPRIME NADA. Las operaciones intermedias no se ejecutan hasta que
 * se invoque una operación terminal.
 */
public class StreamLifecycleAndPipelines {

    /**
     * Demuestra la pereza (laziness) de los streams:
     * Si sólo encadenamos operaciones intermedias sin operación terminal,
     * el contador de ejecuciones permanece en cero.
     */
    public static int demonstrateLazinessWithoutTerminalOp(List<String> names) {
        AtomicInteger executions = new AtomicInteger(0);

        // Se declara el stream, pero NO se ejecuta ninguna operación intermedia todavía
        Stream<String> intermediateStream = names.stream()
                .filter(name -> {
                    executions.incrementAndGet();
                    return name.length() > 3;
                })
                .map(name -> {
                    executions.incrementAndGet();
                    return name.toUpperCase();
                });

        // Retornamos el conteo SIN haber llamado a la operación terminal
        return executions.get(); // Retornará 0
    }

    /**
     * Al agregar la operación terminal (.toList()), el motor del stream
     * jala los elementos uno a uno verticalmente (fusión de operaciones / loop fusion).
     */
    public static PipelineResult demonstrateLoopFusionExecution(List<String> names) {
        List<String> auditLog = new ArrayList<>();

        List<String> result = names.stream()
                .filter(name -> {
                    auditLog.add("filter: " + name);
                    return name.length() > 3;
                })
                .map(name -> {
                    auditLog.add("map: " + name);
                    return name.toUpperCase();
                })
                .limit(2) // Short-circuit: solo toma los primeros 2 que cumplan
                .toList();

        return new PipelineResult(result, auditLog);
    }

    public record PipelineResult(List<String> result, List<String> auditLog) {}

    /**
     * TRAMPA DE ENTREVISTA: Reutilización de un Stream.
     * Un Stream solo se puede consumir UNA VEZ. Intentar consumirlo dos veces
     * arroja java.lang.IllegalStateException: stream has already been operated upon or closed.
     */
    public static void demonstrateStreamReuseThrowsException() {
        Stream<String> stream = Stream.of("Alfa", "Beta", "Gamma");
        stream.forEach(System.out::println); // Primer consumo OK

        // Segundo consumo: Lanza IllegalStateException
        stream.forEach(System.out::println);
    }
}
