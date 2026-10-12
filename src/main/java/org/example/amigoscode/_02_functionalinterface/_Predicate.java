package org.example.amigoscode._02_functionalinterface;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Guía Didáctica: Evolución del filtrado y validación en Java
 * -------------------------------------------------------------
 * Muestra el camino desde métodos imperativos tradicionales hasta
 * la composición funcional y currificación (Higher-Order Functions).
 */
public class _Predicate {

    static void main() {

        // =========================================================================
        // 1. ENFOQUE TRADICIONAL (Imperativo / Métodos estáticos)
        // =========================================================================
        // La lógica está acoplada al método; no se puede pasar como parámetro
        // ni componer dinámicamente con otras reglas en tiempo de ejecución.
        System.out.println("--- 1. Enfoque Tradicional ---");
        System.out.println(isPhoneNumberValid("07000000000")); // true
        System.out.println(isPhoneNumberValid("0700000000"));  // false (longitud != 11)
        System.out.println(isPhoneNumberValid("09009877300")); // false (no inicia con 07)

        // =========================================================================
        // 2. PREDICATE SIMPLE (Aridad 1: Entrada -> boolean)
        // =========================================================================
        // Predicate<T> representa una función de un argumento: T -> boolean.
        // Se ejecuta explícitamente mediante el método .test(valor).
        System.out.println("\n--- 2. Predicate Básico ---");
        System.out.println(isPhoneNumberValidPredicate.test("07000000000")); // true
        System.out.println(isPhoneNumberValidPredicate.test("0700000000"));  // false
        System.out.println(isPhoneNumberValidPredicate.test("09009877300")); // false

        // =========================================================================
        // 3. COMPOSICIÓN DE PREDICADOS (.and / .or / .negate)
        // =========================================================================
        // Permite combinar reglas unarias. Java aplica cortocircuito (short-circuit):
        // si el primer predicado evalúa a false, el segundo ni siquiera se invoca.
        System.out.println("\n--- 3. Composición con .and() ---");
        System.out.println(
                "Válido y contiene '3': " +
                        isPhoneNumberValidPredicate.and(containsNumber3).test("09009877300") // false (falla prefijo)
        );
        System.out.println(
                "Válido y contiene '3': " +
                        isPhoneNumberValidPredicate.and(containsNumber3).test("07000030000") // true (cumple ambos)
        );

        // =========================================================================
        // 4. BIPREDICATE Y SU LIMITACIÓN DE ACOPLAMIENTO (Aridad 2: (T, U) -> boolean)
        // =========================================================================
        // Al encadenar dos BiPredicate con .and(), AMBOS quedan forzados a recibir
        // los mismos dos argumentos en el .test(arg1, arg2).
        // Aquí evalúa: ¿empieza con "5" Y contiene "5"? -> false (empieza con "07").
        System.out.println("\n--- 4. BiPredicate y acoplamiento de parámetros ---");
        System.out.println(
                "Empieza con '5' y contiene '5': " +
                        startWithNumber.and(containsNumber).test("0700050000", "5")
        );

        // =========================================================================
        // 5. CURRIFICACIÓN / HIGHER-ORDER FUNCTION (Function<T, Predicate<R>>)
        // =========================================================================
        // Desacopla la CONFIGURACIÓN de la REGLA del DATO a evaluar:
        //  Paso 1 (.apply): Configura la regla de forma independiente en un closure.
        //  Paso 2 (.and):   Compone libremente los Predicate<String> resultantes.
        //  Paso 3 (.test):  Aplica todos los predicados sobre el teléfono final.
        System.out.println("\n--- 5. Currificación: Configuración independiente ---");
        boolean result = startsWith.apply("07")
                .and(contains.apply("5"))
                .test("07000500000");

        System.out.println("Empieza con '07' y contiene '5': " + result); // true
    }

    // -----------------------------------------------------------------------------
    // DEFINICIONES
    // -----------------------------------------------------------------------------

    /**
     * Enfoque tradicional: Lógica rígida codificada directamente en un método estático.
     */
    static boolean isPhoneNumberValid(String phoneNumber) {
        return phoneNumber.startsWith("07") && phoneNumber.length() == 11;
    }

    /**
     * Regla unaria encapsulada como objeto de primera clase (First-Class Citizen).
     * Puede ser pasada como argumento, almacenada en colecciones o devuelta por métodos.
     */
    static Predicate<String> isPhoneNumberValidPredicate = phoneNumber ->
            phoneNumber.startsWith("07") && phoneNumber.length() == 11;

    /**
     * Predicado con valor rígido ('3' quemado en el código).
     * Desventaja: Poco reutilizable si mañana se necesita buscar el dígito '4'.
     */
    static Predicate<String> containsNumber3 = phoneNumber ->
            phoneNumber.contains("3");

    /**
     * BiPredicate: Recibe 2 parámetros y evalúa una condición booleana.
     * Útil para evaluaciones directas, pero rígido para composición encadenada.
     */
    static BiPredicate<String, String> containsNumber = (String phoneNumber, String number) ->
            phoneNumber.contains(number);

    static BiPredicate<String, String> startWithNumber = (String phoneNumber, String number) ->
            phoneNumber.startsWith(number);

    /**
     * Currificación (Función de orden superior):
     * Recibe el prefijo deseado y RETORNA un Predicate<String> que "recuerda"
     * ese prefijo gracias al contexto léxico (closure).
     * <p>
     * Firma: String -> (String -> boolean)
     */
    static Function<String, Predicate<String>> startsWith =
            prefix -> phone -> phone.startsWith(prefix);

    /**
     * Currificación para búsqueda de contenido:
     * Genera un Predicate a la medida configurado con el valor a buscar.
     * <p>
     * Firma: String -> (String -> boolean)
     */
    static Function<String, Predicate<String>> contains =
            number -> phone -> phone.contains(number);
}