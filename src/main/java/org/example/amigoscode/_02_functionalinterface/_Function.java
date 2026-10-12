package org.example.amigoscode._02_functionalinterface;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Guía Didáctica: Function y BiFunction en Java
 * ---------------------------------------------------------------------
 * MENTAL MODEL:
 * - Function<T, R>: Representa una función que toma UN argumento de tipo T
 *   y produce un resultado de tipo R (Firma: T -> R).
 * - BiFunction<T, U, R>: Representa una función que toma DOS argumentos (T, U)
 *   y produce un resultado de tipo R (Firma: (T, U) -> R).
 *
 * VENTAJAS FRENTE A MÉTODOS ESTÁTICOS TRADICIONALES:
 * - Objetos de primera clase (First-Class Citizens): se pueden guardar en variables,
 *   pasar como argumentos y retornar desde otros métodos.
 * - Composición: permiten construir pipelines complejos encadenando funciones
 *   pequeñas y reutilizables mediante .andThen() y .compose().
 */
public class _Function {
    static void main() {
        // =========================================================================
        // 1. FUNCTION<T, R> BÁSICA VS. MÉTODO TRADICIONAL (Aridad 1)
        // =========================================================================

        // Enfoque tradicional: Invocación directa del método
        int increment = incrementByOne(1);
        System.out.println("Método tradicional (+1): " + increment);

        // Enfoque funcional: La función se ejecuta explícitamente mediante .apply()
        Integer increment2 = incrementByOneFunction.apply(1);
        System.out.println("Function (+1): " + increment2);

        int multiply = multiplyBy10Function.apply(increment2);
        System.out.println("Function (*10): " + multiply);

        // =========================================================================
        // 2. COMPOSICIÓN DE FUNCIONES (.andThen vs. .compose)
        // =========================================================================
        // .andThen(): Ejecuta primero la función actual y luego la suministrada.
        //   f.andThen(g) equivale matemáticamente a: g(f(x))
        //   Aquí: (4 + 1) * 10 = 50
        Function<Integer, Integer> addBy1AndThenMultiplyBy10Function =
                incrementByOneFunction.andThen(multiplyBy10Function);

        System.out.println("Composición con .andThen() para entrada 4: " +
                addBy1AndThenMultiplyBy10Function.apply(4));

        // .compose(): Ejecuta primero la función suministrada y luego la actual.
        //   f.compose(g) equivale a: f(g(x)) -> (4 * 10) + 1 = 41
        Function<Integer, Integer> multiplyBy10AndThenAddBy1 =
                incrementByOneFunction.compose(multiplyBy10Function);
        System.out.println("Composición con .compose() para entrada 4: " +
                multiplyBy10AndThenAddBy1.apply(4));

        // =========================================================================
        // 3. BIFUNCTION<T, U, R> (Aridad 2: dos entradas -> un resultado)
        // =========================================================================
        // Se ejecuta mediante .apply(arg1, arg2).

        // Enfoque tradicional con método de 2 argumentos
        System.out.println("Método de 2 argumentos: " +
                addBy1AndThenMultiplyByNumber(4, 100));

        // Enfoque funcional usando BiFunction
        System.out.println("BiFunction.apply(4, 100): " +
                addBy1AndThenMultiplyByNumberBiFunction.apply(4, 100));

        // Instancia no estática de BiFunction (útil cuando depende del estado de una clase)
        System.out.println("BiFunction a nivel de instancia: " +
                new _Function().addBy1AndThenMultiplyByNumberBiFunctionStaticTest.apply(4, 100));
    }

    // -----------------------------------------------------------------------------
    // DEFINICIONES DE MÉTODOS Y FUNCIONES
    // -----------------------------------------------------------------------------

    /** Método tradicional imperativo de 1 parámetro */
    static int incrementByOne(int number) {
        return number + 1;
    }

    /** Function<T, R>: Función lambda equivalente que incrementa 1 */
    static Function<Integer, Integer> incrementByOneFunction = number -> number + 1;

    /** Function<T, R>: Multiplica el valor por 10 */
    static Function<Integer, Integer> multiplyBy10Function = number -> number * 10;

    /** Método tradicional imperativo de 2 parámetros */
    static int addBy1AndThenMultiplyByNumber(int number, int numToMultiplyBy) {
        return (number + 1) * numToMultiplyBy;
    }

    /**
     * BiFunction<T, U, R>: Recibe dos enteros y retorna el cálculo combinado.
     * Firma: (Integer, Integer) -> Integer
     */
    static BiFunction<Integer, Integer, Integer> addBy1AndThenMultiplyByNumberBiFunction =
            (Integer number, Integer numToMultiplyBy) -> (number + 1) * numToMultiplyBy;

    /** BiFunction como miembro de instancia */
    BiFunction<Integer, Integer, Integer> addBy1AndThenMultiplyByNumberBiFunctionStaticTest =
            (Integer number, Integer numToMultiplyBy) -> (number + 1) * numToMultiplyBy;
}
