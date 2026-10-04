package org.example.module01_foundations;

import java.util.Objects;

/**
 * MÓDULO 01 - CREACIÓN DE INTERFACES FUNCIONALES PERSONALIZADAS
 * -------------------------------------------------------------
 * ¿Cuándo crear una interfaz funcional propia?
 * 1. Cuando la aridad de la función no existe en java.util.function (ej. TriFunction: 3 argumentos).
 * 2. Cuando queremos agregar semántica de dominio enriquecida y métodos default de composición
 *    (ej. Validator<T>, Transformer<T, R>).
 *
 * REGLA FUNDAMENTAL DE SAM:
 * Solo puede haber EXACTAMENTE UN método abstracto.
 * Puede tener cualquier cantidad de métodos default o static.
 */
public class CustomFunctionalInterface {

    /**
     * Interfaz funcional de aridad 3 (Java estándar solo llega hasta BiFunction con 2).
     */
    @FunctionalInterface
    public interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c);

        // Método default para encadenar transformaciones posteriores (Composición)
        default <V> TriFunction<A, B, C, V> andThen(java.util.function.Function<? super R, ? extends V> after) {
            Objects.requireNonNull(after);
            return (A a, B b, C c) -> after.apply(this.apply(a, b, c));
        }
    }

    /**
     * Interfaz funcional orientada a validación de dominio con compositores declarativos.
     */
    @FunctionalInterface
    public interface Validator<T> {
        ValidationResult validate(T value);

        default Validator<T> and(Validator<T> other) {
            return value -> {
                ValidationResult result = this.validate(value);
                return !result.isValid() ? result : other.validate(value);
            };
        }

        static <T> Validator<T> notNull(String fieldName) {
            return value -> value != null
                    ? ValidationResult.ok()
                    : ValidationResult.error(fieldName + " no puede ser nulo");
        }
    }

    public record ValidationResult(boolean isValid, String message) {
        public static ValidationResult ok() {
            return new ValidationResult(true, "");
        }

        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }
    }
}
