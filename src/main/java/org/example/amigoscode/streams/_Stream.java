package org.example.amigoscode.streams;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.example.amigoscode.streams._Stream.Gender.*;

public class _Stream {
    public static void main(String[] args) {
        List<Person> people = List.of(
                new Person("John", MALE),
                new Person("Aisha", FEMALE),
                new Person("Alex", MALE),
                new Person("Alice", FEMALE)
        );

        // =========================================================================
        // 1. TRANSFORMACIONES BÁSICAS Y COLECCIONES
        // =========================================================================

        System.out.println("=== 1. Géneros únicos presentes (Set) ===");
        // Transforma cada Person a Gender y elimina duplicados mediante un Set
        Set<Gender> uniqueGenders = people.stream()
                .map(person -> person.gender)
                .collect(Collectors.toSet());

        uniqueGenders.forEach(gender -> System.out.println("• " + gender));

        System.out.println("\n=== 2. Listado de nombres ===");
        // Extrae únicamente los nombres a una lista inmutable
        List<String> names = people.stream()
                .map(person -> person.name)
                .toList();

        names.forEach(name -> System.out.println("• " + name));

        // =========================================================================
        // 2. DESGLOSE PEDAGÓGICO DE INTERFACES FUNCIONALES
        // =========================================================================

        System.out.println("\n=== 3. Deconstrucción de operaciones intermedias y terminales ===");

        // Function<T, R>: toma una instancia de Person y extrae su nombre (String)
        Function<Person, String> extractNameFunction = person -> person.name;

        // Function<T, R>: toma un String y calcula su longitud (Integer) mediante method reference
        Function<String, Integer> calculateLengthFunction = String::length;

        // Consumer<T>: operación terminal que recibe el resultado y produce un efecto secundario (I/O)
        Consumer<Integer> printLengthConsumer = length -> System.out.println("• Longitud del nombre: " + length + " caracteres");

        // Pipeline compuesto conectando las interfaces funcionales explícitas
        people.stream()
                .map(extractNameFunction)
                .map(calculateLengthFunction)
                .forEach(printLengthConsumer);

        // =========================================================================
        // 3. EVALUACIÓN DE PREDICADOS (Short-circuiting terminal operations)
        // =========================================================================

        System.out.println("\n=== 4. Verificación de condiciones (Predicados) ===");

        Predicate<Person> isFemale = person -> FEMALE.equals(person.gender);
        Predicate<Person> prefersNotToSay = person -> PREFER_NOT_TO_SAY.equals(person.gender);

        // allMatch: retorna true si TODOS los elementos satisfacen la condición
        boolean containsOnlyFemales = people.stream().allMatch(isFemale);
        System.out.println("¿Todas las personas son mujeres? (allMatch): " + containsOnlyFemales);

        // anyMatch: retorna true si AL MENOS UN elemento satisface la condición
        boolean containsAnyFemale = people.stream().anyMatch(isFemale);
        System.out.println("¿Hay al menos una mujer en la lista? (anyMatch): " + containsAnyFemale);

        // noneMatch: retorna true si NINGÚN elemento satisface la condición
        boolean nonePrefersNotToSay = people.stream().noneMatch(prefersNotToSay);
        System.out.println("¿Nadie eligió 'PREFER_NOT_TO_SAY'? (noneMatch): " + nonePrefersNotToSay);
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
        MALE, FEMALE, PREFER_NOT_TO_SAY
    }
}