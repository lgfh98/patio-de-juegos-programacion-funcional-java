package org.example.amigoscode._06_finalsection;

import java.util.function.BiFunction;

/**
 * Guía Didáctica: Expresiones Lambda en Java
 * -------------------------------------------------------------
 * MENTAL MODEL:
 * - Una expresión Lambda es una función anónima y concisa que implementa
 *   el método abstracto único de una Interfaz Funcional (Single Abstract Method - SAM).
 *
 * VARIANTES SINTÁCTICAS:
 * 1. Expresión simple (One-liner):
 *    - (x, y) -> x + y
 *    - No requiere llaves ni palabra clave 'return'; el valor de la expresión se retorna implícitamente.
 *
 * 2. Bloque de sentencias (Block body):
 *    - (x, y) -> { sentencia1; sentencia2; return resultado; }
 *    - Requiere llaves {} y la sentencia explícita 'return' si la interfaz espera un resultado.
 *
 * FUNCIONES PURAS VS. EFECTOS SECUNDARIOS:
 * - Función Pura:
 *   * Determinista: Mismos argumentos de entrada producen SIEMPRE el mismo resultado.
 *   * Sin efectos secundarios (Side effects): no modifica estado externo, ni realiza I/O (consola, red, BD).
 * - En el ejemplo abajo, la lambda realiza validación previa y produce un efecto secundario
 *   (System.out.println), convirtiéndola en una función impura. En diseño funcional idiomático,
 *   se recomienda separar la lógica pura de los efectos secundarios.
 */
public class Lambdas {

    static void main() {
        // =========================================================================
        // 1. LAMBDA CON BLOQUE DE CÓDIGO (Multi-line Lambda)
        // =========================================================================
        // BiFunction<T, U, R>: Recibe (String name, Integer age) y retorna String
        BiFunction<String, Integer, String> upperCaseName = (name, age) -> {
            // Validación de entrada (Regla de negocio)
            if (name.isBlank()) {
                throw new IllegalArgumentException("El nombre no puede estar vacío");
            }

            // Efecto secundario: Imprime la edad en consola (I/O)
            System.out.println("Edad del usuario: " + age);

            // Transformación pura retornada
            return name.toUpperCase();
        };

        // =========================================================================
        // 2. EJECUCIÓN CON .apply()
        // =========================================================================
        System.out.println("Resultado de la lambda: " + upperCaseName.apply("Luis", 20));

        // =========================================================================
        // 3. COMPARATIVA: LAMBDA PURA EN UNA SOLA LÍNEA (One-liner)
        // =========================================================================
        // Sin llaves, sin return explícito, sin efectos secundarios
        BiFunction<String, Integer, String> pureUpperCase = (name, age) -> name.toUpperCase() + " (" + age + ")";
        System.out.println("Lambda pura concisa: " + pureUpperCase.apply("Luis", 20));
    }
}
