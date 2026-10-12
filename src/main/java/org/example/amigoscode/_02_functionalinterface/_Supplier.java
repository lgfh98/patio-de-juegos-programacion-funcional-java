package org.example.amigoscode._02_functionalinterface;

import java.util.List;
import java.util.function.Supplier;

/**
 * Guía Didáctica: Supplier en Java
 * -------------------------------------------------------------
 * MENTAL MODEL:
 * - Supplier<T>: Representa un proveedor de resultados que NO recibe ningún argumento
 *   y produce un valor de tipo T (Firma: () -> T).
 *
 * EVALUACIÓN PEREZOSA (LAZY EVALUATION):
 * - Un método tradicional o variable evalúa y calcula su valor inmediatamente (Eager).
 * - Un Supplier encapsula una expresión o cómputo diferido: el código NO se ejecuta
 *   hasta el momento exacto en que se invoca su método .get().
 *
 * CASOS DE USO CRUCIALES EN JAVA MODERNO:
 * 1. Fallbacks seguros: Optional.orElseGet(Supplier) — solo ejecuta el fallback si está vacío.
 * 2. Logging condicional: evita construir strings costosos si el nivel de log está apagado.
 * 3. Asincronía: CompletableFuture.supplyAsync(Supplier).
 * 4. Patrones Factory / Singleton con inicialización perezosa (Lazy Initialization).
 */
public class _Supplier {
    static void main() {
        // =========================================================================
        // 1. MÉTODO TRADICIONAL (Eager: Se evalúa al ser invocado)
        // =========================================================================
        System.out.println("=== 1. Método tradicional ===");
        System.out.println(getDBConnectionUrl());

        // =========================================================================
        // 2. SUPPLIER<T> (Lazy: La lambda solo se ejecuta al llamar a .get())
        // =========================================================================
        System.out.println("\n=== 2. Supplier<List<String>> (.get()) ===");
        // La lista no se construye en memoria hasta que llamamos a .get()
        System.out.println(getDBConnectionUrlsSupplier.get());

        // Ejemplo didáctico: Demostración de ejecución bajo demanda
        System.out.println("\n=== 3. Demostración de ejecución bajo demanda ===");
        Supplier<Double> expensiveCalculation = () -> {
            System.out.println("-> [Cálculo costoso en progreso...]");
            return Math.PI * 2;
        };

        System.out.println("El Supplier está definido, pero aún no se ha ejecutado.");
        System.out.println("Llamando a .get() ahora:");
        System.out.println("Resultado: " + expensiveCalculation.get());
    }

    // -----------------------------------------------------------------------------
    // DEFINICIONES DE MÉTODOS Y PROVEEDORES
    // -----------------------------------------------------------------------------

    /** Método tradicional que devuelve inmediatamente una URL */
    static String getDBConnectionUrl() {
        return "jdbc://localhost:5432/users";
    }

    /**
     * Supplier<List<String>>: Proveedor funcional que genera una lista de URLs
     * únicamente cuando alguien solicita .get().
     * Firma: () -> List<String>
     */
    static Supplier<List<String>> getDBConnectionUrlsSupplier = () -> List.of(
            "jdbc://localhost:5432/users",
            "jdbc://localhost:5432/customer"
    );
}

