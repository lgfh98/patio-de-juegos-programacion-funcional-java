package org.example.module01_foundations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * MÓDULO 01 - FUNCIONES PURAS E INMUTABILIDAD
 * ----------------------------------------------------
 * Dos pilares indispensables de la Programación Funcional:
 *
 * 1. Función Pura:
 *    - Transparencia referencial: para los mismos argumentos de entrada, SIEMPRE produce el mismo resultado.
 *    - Cero efectos secundarios (Side Effects): no muta variables externas, no altera los argumentos recibidos,
 *      no realiza I/O no controlada dentro del cálculo.
 *
 * 2. Inmutabilidad:
 *    - Los objetos una vez creados nunca cambian su estado.
 *    - Favorece la seguridad en hilos (thread-safety) sin locks y previene bugs sutiles en streams.
 */
public class PureFunctionsAndImmutability {

    // ESTADO MUTABLE EXTERNO (Anti-patrón funcional: Función impura)
    private static int globalCounter = 0;

    /**
     * IMPURA:
     * - Depende y altera un estado global compartido.
     * - Si se llama en paralelo desde múltiples hilos, produce race conditions.
     */
    public static int impureIncrementAndAdd(int value) {
        globalCounter++;
        return value + globalCounter;
    }

    /**
     * PURA:
     * - Solo depende de sus parámetros de entrada.
     * - No tiene efectos secundarios.
     * - Es determinista y trivial de testear.
     */
    public static int pureAdd(int a, int b) {
        return a + b;
    }

    // CLASE INMUTABLE
    // Una clase inmutable en Java (o un record) debe:
    // 1. Ser final o con campos private final.
    // 2. Si contiene colecciones o referencias mutables, hacer copias defensivas al construir y al retornar.
    public record ImmutableCart(String customerId, List<String> items) {
        public ImmutableCart {
            // Copia defensiva inmutable para evitar que modificaciones externas afecten este objeto
            items = List.copyOf(items);
        }

        /**
         * En FP, en lugar de mutar 'this.items.add(...)', generamos una nueva instancia
         * con el nuevo estado (Evolución de estado inmutable).
         */
        public ImmutableCart withItem(String newItem) {
            List<String> updated = new ArrayList<>(this.items);
            updated.add(newItem);
            return new ImmutableCart(this.customerId, updated);
        }
    }
}
