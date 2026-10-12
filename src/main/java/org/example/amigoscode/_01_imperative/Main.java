package org.example.amigoscode._01_imperative;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Guía Didáctica: Enfoque Imperativo vs. Enfoque Declarativo (Funcional)
 * ---------------------------------------------------------------------
 * Comparación fundamental entre dos formas de resolver un problema:
 *
 * 1. Imperativo ("CÓMO"):
 *    - Le indicamos a la máquina paso a paso cada instrucción (bucle for, if, acumulador).
 *    - Maneja estado mutable explícito (creación de ArrayList y llamadas a .add).
 *    - Mayor propensión a errores (índices, mutabilidad accidental, concurrencia difícil).
 *
 * 2. Declarativo / Funcional ("QUÉ"):
 *    - Expresamos la intención del resultado deseado mediante abstracciones de alto nivel.
 *    - No hay variables mutables intermedias.
 *    - El pipeline de Streams permite fácil paralelización y composición limpia.
 */
public class Main {
    static void main() {
        List<Person> people = List.of(
                new Person("John", Gender.MALE),
                new Person("Maria", Gender.FEMALE),
                new Person("Aisha", Gender.FEMALE),
                new Person("Alex", Gender.MALE),
                new Person("Alice", Gender.FEMALE)
        );

        // =========================================================================
        // 1. ENFOQUE IMPERATIVO ("CÓMO resolver el problema")
        // =========================================================================
        System.out.println("=== Enfoque Imperativo ===");

        // Paso 1: Crear una estructura de datos mutable para acumular resultados
        List<Person> females = new ArrayList<>();

        // Paso 2: Iterar manualmente sobre cada elemento de la colección
        for (Person person : people) {
            // Paso 3: Aplicar manualmente la condición de filtrado
            if (Gender.FEMALE.equals(person.gender)) {
                // Paso 4: Modificar el estado agregando el elemento
                females.add(person);
            }
        }

        // Paso 5: Iterar manualmente de nuevo para imprimir cada elemento
        for (Person female : females) {
            System.out.println(female);
        }

        // =========================================================================
        // 2. ENFOQUE DECLARATIVO ("QUÉ resultado esperamos")
        // =========================================================================
        System.out.println("\n=== Enfoque Declarativo ===");

        // - .stream(): Abre un canal de flujo de datos sobre la colección.
        // - .filter(Predicate): Define la regla de negocio sin bucles manuales.
        // - .toList(): Recolecta el flujo en una lista inmutable (Java 16+).
        List<Person> females2 = people.stream()
                .filter(person -> Gender.FEMALE.equals(person.gender))
                .toList();

        // - .forEach(Consumer): Recorre la colección pasando una referencia a método (Method Reference).
        females2.forEach(System.out::println);
    }

    static class Person {
        private final String name;
        private final Gender gender;

        Person(String name, Gender gender) {
            this.name = name;
            this.gender = gender;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", gender=" + gender +
                    '}';
        }
    }

    enum Gender {
        MALE, FEMALE
    }
}
