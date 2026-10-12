package org.example.amigoscode._04_optionals;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Guía Didáctica: Manejo de Ausencia con Java Optional
 * -------------------------------------------------------------
 * MENTAL MODEL:
 * - Optional<T> es un contenedor inmutable que puede contener un valor no nulo
 *   o estar vacío. Su objetivo es eliminar el clásico NullPointerException y hacer
 *   explícita la posibilidad de ausencia en las firmas de los métodos.
 *
 * MÉTODOS CLAVE ILUSTRADOS EN ESTA CLASE:
 * 1. orElseThrow(Supplier<X>):
 *    - Obtiene el valor si está presente.
 *    - Si está vacío, lanza la excepción provista por el Supplier.
 *    - ¿Por qué Supplier? Para instanciar la excepción de forma PEREZOSA (Lazy),
 *      evitando el coste computacional de capturar el stacktrace si el valor sí existe.
 *
 * 2. ifPresentOrElse(Consumer<T>, Runnable):
 *    - Introducido en Java 9.
 *    - Proporciona un flujo declarativo equivalente al clásico bloque if-else:
 *      * Si hay valor -> invoca el Consumer<T>.
 *      * Si está vacío -> invoca la acción de fallback en el Runnable.
 *
 * (Nota: Para un desglose exhaustivo con casos reales en Spring y anti-patrones,
 * ver org.example.amigoscode._04_optionals.dedicatedvideo._Optional).
 */
public class _Optional {
    static void main() {
        // =========================================================================
        // 1. MANEJO DE EXCEPCIONES PEREZOSAS: orElseThrow CON SUPPLIER
        // =========================================================================
        System.out.println("=== 1. orElseThrow con Supplier diferido ===");

        // El Supplier solo instanciará la excepción SI y SOLO SI el Optional está vacío
        Supplier<IllegalStateException> exception = () ->
                new IllegalStateException("exception custom message");

        // Caso A: Valor presente -> Retorna "Hello" sin invocar el Supplier
        try {
            Object value = Optional.ofNullable("Hello")
                    .orElseThrow(exception);
            System.out.println("Valor obtenido exitosamente: " + value);
        } catch (IllegalStateException e) {
            System.out.println("Excepción: " + e.getMessage());
        }

        // Caso B: Valor ausente (null) -> Dispara el Supplier y lanza la excepción
        try {
            Object emptyValue = Optional.ofNullable(null)
                    .orElseThrow(exception);
            System.out.println("Valor obtenido: " + emptyValue);
        } catch (IllegalStateException e) {
            System.out.println("Excepción disparada por ausencia: " + e.getMessage());
        }

        // =========================================================================
        // 2. CONTROL DE FLUJO DECLARATIVO: ifPresentOrElse (Java 9+)
        // =========================================================================
        System.out.println("\n=== 2. ifPresentOrElse(Consumer, Runnable) ===");

        // Caso con valor null: activa la rama Runnable
        Optional.ofNullable(null)
                .ifPresentOrElse(
                        email -> {
                            // Rama if: Se ejecuta si el valor está presente (Consumer)
                            System.out.println("Sending message to " + email);
                        },
                        () -> {
                            // Rama else: Se ejecuta si el valor está ausente (Runnable)
                            System.out.println("Cannot send email: email es nulo o ausente");
                        }
                );
    }
}
