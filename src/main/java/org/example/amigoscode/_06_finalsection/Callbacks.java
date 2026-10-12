package org.example.amigoscode._06_finalsection;

import java.util.function.Consumer;

/**
 * Guía Didáctica: Callbacks en Java al estilo JavaScript
 * -------------------------------------------------------------
 * MENTAL MODEL:
 * - Un "Callback" es una función que se pasa como argumento a otra función
 *   para ser ejecutada ("llamada de vuelta") cuando ocurre un evento o condición.
 *
 * ¿CÓMO SE LOGRA EN JAVA?
 * - En lenguajes dinámicos como JavaScript, las funciones son nativamente valores de primera clase.
 * - En Java clásico (anterior a Java 8), los callbacks requerían clases anónimas (ej. new Runnable() {}).
 * - A partir de Java 8+, usamos INTERFACES FUNCIONALES estándar:
 *   1. Consumer<T>: Para callbacks que esperan recibir un argumento de vuelta (callback.accept(dato)).
 *   2. Runnable: Para callbacks que solo señalan una acción sin recibir ni devolver parámetros (callback.run()).
 */
public class Callbacks {

    static void main() {
        // =========================================================================
        // 1. CALLBACK CON ARGUMENTO: Consumer<T> (callback.accept(value))
        // =========================================================================
        System.out.println("=== 1. Callback con Consumer (pasa dato al callback) ===");
        hello("Luis", null, value -> {
            System.out.println("Callback ejecutado: no se proporcionó apellido para " + value);
        });

        // Caso donde NO se dispara el callback porque existe el apellido
        hello("Maria", "Pérez", value -> {
            System.out.println("Este callback no se ejecutará");
        });

        // =========================================================================
        // 2. CALLBACK SIN ARGUMENTO: Runnable (callback.run())
        // =========================================================================
        System.out.println("\n=== 2. Callback con Runnable (simple notificación) ===");
        hello2("Luis", null, () -> {
            System.out.println("Callback ejecutado: acción ejecutada sin necesidad de parámetros");
        });
    }

    /**
     * Emula un callback con paso de parámetro:
     * Si 'lastName' es null, se invoca callback.accept(firstName).
     */
    static void hello(String firstName, String lastName, Consumer<String> callback) {
        System.out.println("Nombre: " + firstName);
        if (lastName != null) {
            System.out.println("Apellido: " + lastName);
        } else {
            // Invocación del callback pasando el parámetro contextual
            callback.accept(firstName);
        }
    }

    /**
     * Emula un callback simple sin parámetros (estilo Runnable):
     * Si 'lastName' es null, se dispara callback.run().
     */
    static void hello2(String firstName, String lastName, Runnable callback) {
        System.out.println("Nombre: " + firstName);
        if (lastName != null) {
            System.out.println("Apellido: " + lastName);
        } else {
            // Invocación del callback sin parámetros
            callback.run();
        }
    }

    /*
     * COMPARATIVA CON JAVASCRIPT:
     *
     * En JS no existen tipos formales en la firma:
     * function hello(firstName, lastName, callback) {
     *     console.log(firstName);
     *     if (lastName) {
     *         console.log(lastName);
     *     } else {
     *         callback(firstName); // En Java: callback.accept(firstName);
     *     }
     * }
     */
}
