package org.example.module01_foundations;

import java.util.ArrayList;
import java.util.List;

/**
 * MÓDULO 01 - FUNDAMENTOS
 * ----------------------------------------------------
 * Comparación: Paradigma Imperativo vs Paradigma Declarativo (Funcional).
 *
 * MENTAL MODEL:
 * - Imperativo: "CÓMO se hace" paso a paso (control manual de bucles, estados mutables, contadores temporales).
 * - Declarativo: "QUÉ resultado queremos" (composición de funciones puras, inmutabilidad, pipes de transformación).
 */
public class ImperativeVsDeclarative {

    public record User(String name, int age, boolean active) {}

    /**
     * Enfoque Imperativo tradicional:
     * - Inicializa acumulador mutable (result).
     * - Bucle for-each para iterar.
     * - Sentencia condicional if para filtrar.
     * - Mutación explícita de estado (.add).
     */
    public static List<String> getActiveAdultUserNamesImperative(List<User> users) {
        if (users == null) return List.of();

        List<String> result = new ArrayList<>();
        for (User user : users) {
            if (user.active() && user.age() >= 18) {
                result.add(user.name().toUpperCase());
            }
        }
        return result;
    }

    /**
     * Enfoque Declarativo / Funcional:
     * - Sin variables mutables intermedias.
     * - Pipeline de transformaciones con intención explícita:
     *   1. filter: define la regla de negocio de elegibilidad.
     *   2. map: define la transformación del dato (proyección a nombre en mayúsculas).
     *   3. toList: recolecta el resultado final en una lista inmutable.
     */
    public static List<String> getActiveAdultUserNamesDeclarative(List<User> users) {
        if (users == null) return List.of();

        return users.stream()
                .filter(User::active)
                .filter(user -> user.age() >= 18)
                .map(user -> user.name().toUpperCase())
                .toList(); // En Java 16+, toList() produce una lista inmutable no modificable
    }
}
