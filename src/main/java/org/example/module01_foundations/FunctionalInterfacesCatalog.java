package org.example.module01_foundations;

import java.util.function.*;

/**
 * MÓDULO 01 - EL CATÁLOGO DE INTERFACES FUNCIONALES (java.util.function)
 * ----------------------------------------------------------------------
 * En Java, una Interfaz Funcional es una interfaz con EXACTAMENTE UN método abstracto (SAM: Single Abstract Method).
 * La anotación @FunctionalInterface es opcional para el compilador, pero recomendada para documentar y prevenir errores.
 *
 * Los 4 Pilares Fundamentales:
 * 1. Predicate<T>        : T -> boolean          (Filtros, validaciones, condiciones)
 * 2. Function<T, R>       : T -> R                (Transformaciones, proyecciones, mapeos)
 * 3. Consumer<T>          : T -> void             (Efectos secundarios controlados, logs, notificaciones)
 * 4. Supplier<T>          : () -> T               (Evaluación perezosa / Lazy, factories)
 *
 * Variaciones clave:
 * - BiPredicate<T, U>     : (T, U) -> boolean
 * - BiFunction<T, U, R>   : (T, U) -> R
 * - BiConsumer<T, U>      : (T, U) -> void
 * - UnaryOperator<T>      : T -> T  (Especialización de Function<T, T>)
 * - BinaryOperator<T>     : (T, T) -> T (Especialización de BiFunction<T, T, T>)
 */
public class FunctionalInterfacesCatalog {

    // 1. PREDICATE: Evaluación booleana y composición (and, or, negate)
    public static final Predicate<String> isEmailValid = email -> email != null && email.contains("@");
    public static final Predicate<String> hasCorporateDomain = email -> email != null && email.endsWith("@empresa.com");

    // Composición funcional de Predicates:
    public static final Predicate<String> isCorporateEmail = isEmailValid.and(hasCorporateDomain);
    public static final Predicate<String> isNonCorporateEmail = isCorporateEmail.negate();

    // 2. FUNCTION: Transformación de tipos y encadenamiento (andThen, compose)
    public static final Function<String, Integer> stringLength = String::length;
    public static final Function<Integer, String> formatReport = len -> "Longitud calculada: " + len;

    // andThen: primero stringLength, luego formatReport -> f(g(x))
    public static final Function<String, String> lengthFormatter = stringLength.andThen(formatReport);

    // compose: primero g, luego f -> f(g(x))
    public static final Function<String, String> reverseComposeExample = formatReport.compose(stringLength);

    // 3. UNARY & BINARY OPERATORS: Entran y salen del mismo tipo
    public static final UnaryOperator<String> sanitizeInput = input -> input == null ? "" : input.trim().toLowerCase();
    public static final BinaryOperator<Double> calculateTotalWithDiscount = (price, discountPercent) -> price * (1.0 - discountPercent / 100.0);

    // 4. CONSUMER: Acción / Consumo sin retorno
    public static Consumer<String> createConsoleLogger(String prefix) {
        return message -> System.out.println("[" + prefix + "] " + message);
    }

    // Composición de Consumers con andThen:
    public static Consumer<String> chainConsumers(Consumer<String> first, Consumer<String> second) {
        return first.andThen(second);
    }

    // 5. SUPPLIER: Provisión diferida (Lazy Evaluation)
    // CRUCIAL: El cálculo pesado NO se ejecuta hasta que alguien llama a .get()
    public static Supplier<Double> heavyComputationSupplier() {
        return () -> {
            // Simulación de cálculo costoso
            double sum = 0.0;
            for (int i = 0; i < 1_000_000; i++) {
                sum += Math.sqrt(i);
            }
            return sum;
        };
    }
}
