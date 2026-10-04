package org.example.module04_optional_mastery;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * MÓDULO 04 - TRAMPAS CLÁSICAS DE ENTREVISTA CON OPTIONAL
 * -----------------------------------------------------------------------------
 * 1. orElse vs orElseGet:
 *    - orElse(T other): El argumento 'other' se evalúa SIEMPRE de forma EAGER (ansiosa),
 *      incluso si el Optional TIENE un valor presente.
 *      Si 'other' es una llamada a base de datos, una llamada REST o una instanciación pesada,
 *      SE EJECUTARÁ INNECESARIAMENTE cada vez.
 *    - orElseGet(Supplier<? extends T> supplier): Se evalúa de forma LAZY (perezosa).
 *      El Supplier SOLO se invoca si el Optional está vacío.
 *
 * 2. orElseThrow():
 *    - En Java 10+, opt.orElseThrow() es la forma idiomática y preferida sobre opt.get(),
 *      evitando sonar como que el valor estuviera garantizado.
 *
 * 3. Anti-patrones de Arquitectura en APIs:
 *    - NUNCA usar Optional como parámetro de método (obliga al cliente a envolver en Optional.ofNullable).
 *    - NUNCA usar Optional como campo (field) de entidad JPA o DTO (Optional no implementa Serializable).
 *    - NUNCA retornar null en un método que retorna Optional (retornar siempre Optional.empty()).
 */
public class OptionalInterviewTraps {

    /**
     * Demostrador de la trampa orElse vs orElseGet.
     */
    public static class TrapDemonstrator {
        private final AtomicInteger expensiveServiceCallCounter = new AtomicInteger(0);

        public String callExpensiveExternalDatabaseService() {
            expensiveServiceCallCounter.incrementAndGet();
            return "DATOS_DE_RESPALDO_COSTOSOS";
        }

        public int getExpensiveCallsCount() {
            return expensiveServiceCallCounter.get();
        }

        /**
         * ERROR COMÚN:
         * Aunque 'presentValue' NO esté vacío, 'callExpensiveExternalDatabaseService()'
         * SE EJECUTA de todos modos porque Java evalúa los argumentos antes de invocar el método.
         */
        public String resolveWithOrElse(Optional<String> presentValue) {
            return presentValue.orElse(callExpensiveExternalDatabaseService());
        }

        /**
         * BUENA PRÁCTICA (LAZY):
         * Al usar un Supplier lambda (orElseGet), Java solo ejecuta la función
         * si presentValue es empty.
         */
        public String resolveWithOrElseGet(Optional<String> presentValue) {
            return presentValue.orElseGet(this::callExpensiveExternalDatabaseService);
        }
    }
}
