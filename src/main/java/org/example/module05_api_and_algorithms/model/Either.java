package org.example.module05_api_and_algorithms.model;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * MÓNADA EITHER: El estándar de la industria para manejo funcional de errores.
 * ----------------------------------------------------------------------------
 * Representa un valor de uno de dos posibles tipos:
 * - Left (Izquierda): Representa la falla o error (por convención en FP).
 * - Right (Derecha): Representa el éxito ("Right" en inglés también significa "correcto").
 *
 * Permite que un stream procese elementos sin explotar ante una excepción,
 * acumulando éxitos y fallas de forma declarativa.
 */
public sealed interface Either<L, R> permits Either.Left, Either.Right {

    record Left<L, R>(L value) implements Either<L, R> {}
    record Right<L, R>(R value) implements Either<L, R> {}

    static <L, R> Either<L, R> left(L value) {
        return new Left<>(value);
    }

    static <L, R> Either<L, R> right(R value) {
        return new Right<>(value);
    }

    default boolean isRight() {
        return this instanceof Right<L, R>;
    }

    default boolean isLeft() {
        return this instanceof Left<L, R>;
    }

    default Optional<R> getRight() {
        return this instanceof Right<L, R> r ? Optional.of(r.value()) : Optional.empty();
    }

    default Optional<L> getLeft() {
        return this instanceof Left<L, R> l ? Optional.of(l.value()) : Optional.empty();
    }

    default <T> Either<L, T> map(Function<? super R, ? extends T> mapper) {
        if (this instanceof Right<L, R> r) {
            return Either.right(mapper.apply(r.value()));
        }
        @SuppressWarnings("unchecked")
        Left<L, T> left = (Left<L, T>) this;
        return left;
    }

    default void ifLeft(Consumer<? super L> action) {
        if (this instanceof Left<L, R> l) {
            action.accept(l.value());
        }
    }

    default void ifRight(Consumer<? super R> action) {
        if (this instanceof Right<L, R> r) {
            action.accept(r.value());
        }
    }
}
